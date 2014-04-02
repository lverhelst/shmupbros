package Game.Entity;

import Game.AIManager.MyThread;
import Ai.AStar;
import Ai.FuzzyOperator;
import Ai.Ray;
import Ai.FuzzySet;
import Game.State.GameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import Game.Map.Tile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.newdawn.slick.geom.Polygon;

/**
 * @author Leon Verhelst and Emery
 */
public class Bot extends Playable {
    public ArrayList<Tile> path;
    private Playable target; 
    private static AStar astar;
    private static Lock lock = new ReentrantLock();
    
    //fuzzy logic attributes
    private Ray primaryRay, secondaryRay, targetRay, frontRay;
    private double weight, weight2, weight3;
    private double fireRate, turnRate, moveRate, learnRate;
    private double slow, normal, fast, left, facing, right;
    private FuzzySet fin;
    
    public Bot(float f){
        super(f);
        super.setColor(Color.orange);
        path = new ArrayList<>();
        
        if(astar == null)
            astar = new AStar();              
        
        //used to detect distances to collisions
        primaryRay = new Ray();
        secondaryRay = new Ray();
        targetRay = new Ray();
        frontRay = new Ray();
        
        //used to give weights to fuzzy move logic
        weight = 1;
        weight2 = 0.1;
        weight3 = 0.1;
        
        learnRate = 16;
        
    }
    
    /**
     * @return the target
     */
    public Playable getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Playable target) {
        this.target = target;
        
        GameState.addText(this.getIdentifier() + " targeted " + target.getIdentifier());
    }
    
    public void generatePathToTarget(){
        //generate route to target
        if(GameState.getMap() != null){
            if(astar.getMap() == null){
                astar.setMap(GameState.getMap());
            }
            //Ensure only one thread can generate a path at a time
            //This ensures high performance
            if (lock.tryLock()) {
               try {
                   // long running process       
                    ((MyThread)Thread.currentThread()).last_locked = System.nanoTime();
                    path = astar.pathFind(this, target);  
                                      
                    if(path != null){
                        for(Tile t : path){   
                            t.pathnode = true;
                        }   
                    }                    
               } catch (Exception e) {                   
               } finally {                   
                   lock.unlock();
               } 
            }
        }
    }
    
    /**
     * Used to check if the bot currently has a path
     * @return true if a path exists
     */
    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }
    
    /**
     * Chooses random Entity as target
     */
    public void chooseRandTarget() {
        
        Random rng = new Random();
        int size = GameState.getEntities().size();
        int answer = rng.nextInt(size);
        this.fireRate = 0.0;
        //Will find a playable object which is alive randomly
        for(Physical p : GameState.getEntities()){
            if(p.getType() == TYPE.PLAYABLE && ((Playable)p).isAlive() && rng.nextInt(size) == answer && ((Playable)p) != this)
                setTarget((Playable)p);
        }
    }
    
    /**
     * @return double of the calculated move speed
     */
    public double getMoveRate() {
        return moveRate;
    }
    
    /**
     * @return double of the calculated fire speed
     */
    public double getFireRate() {
        return fireRate;
    }
    
    /**
     * @return double of the calculated turn speed
     */
    public double getTurnRate() {
        return turnRate;
    }
    
    @Override public void Collide() {
        super.Collide();
        
        //System.out.println(slow + " " + normal + " " + fast);
        //System.err.println(getWeight() + " " + getWeight2() + " " + getWeight3());
        
        //reward
        weight += (getWeight() - (getWeight() * slow))/learnRate;
        weight2 += (getWeight2() - (getWeight2() * normal))/learnRate;
        weight3 += (getWeight3() - (getWeight3() * fast))/learnRate;
        
        //punish
        weight -= (getWeight() * slow)/learnRate;
        weight2 -= (getWeight2() * normal)/learnRate;
        weight3 -= (getWeight3() * fast)/learnRate;
        
        //bound weights
        weight = Math.min(Math.max(getWeight(), 0),1);
        weight2 = Math.min(Math.max(getWeight2(), 0),1);
        weight3 = Math.min(Math.max(getWeight3(), 0),1);
    }
    
    /**
     * Used to update the bots position and run the fuzzy logic for the bot
     */
    @Override public void update() {
        super.update();
        
        if(!target.isAlive())
            chooseRandTarget();
        
        applyFuzzy();
    }
    
    public void applyFuzzy() {
        //cast the rays to use in fuzzy logic
        primaryRay.cast(this, 5, 8, 1);
        secondaryRay.cast(this, -5, 8, 2);
        boolean rhit = new Ray().cast(this, 1, 8, 0);
        boolean hit = targetRay.cast(this, target, 32);
        
        double distance1 = primaryRay.getDistance();
        double distance2 = secondaryRay.getDistance();
        
        FuzzySet Rclose = GameState.getRule("Close");
        FuzzySet Rmiddle = GameState.getRule("Middle");
        FuzzySet Rfar = GameState.getRule("Far");
        
        FuzzySet Rsmall = GameState.getRule("Small");
        FuzzySet Rmedium = GameState.getRule("Medium");
        FuzzySet Rlarge = GameState.getRule("Large");
        
        FuzzySet Rleft = GameState.getRule("Left");
        FuzzySet Rfacing = GameState.getRule("Facing");
        FuzzySet Rright = GameState.getRule("Right");
        
        FuzzySet Rslow = GameState.getRule("Slow");
        FuzzySet Rnormal = GameState.getRule("Normal");
        FuzzySet Rfast = GameState.getRule("Fast");
        
        double rotationToNodeVector = 0;
        double smallAngle = 0, normalAngle = 0, largeAngle = 0;
        
        //check angle to A* path
        if(hasPath()) {            
            rotationToNodeVector = getRotationToEntity(path.get(path.size() - 1));
            
            if(path.size() > 2) { //&& bot can see node 2
               rotationToNodeVector = (rotationToNodeVector + getRotationToEntity(path.get(path.size() - 2)))/2;
               if(Math.abs(rotationToNodeVector) < 5){
                   path.remove(path.size() - 1);
                   path.remove(path.size() - 2);
               }
            }
            
            double rotation = rotationToNodeVector - getRotation() % 180;
            
            smallAngle = Rsmall.evaluate(rotation);
            normalAngle = Rmedium.evaluate(rotation);
            largeAngle = Rlarge.evaluate(rotation);
        }
        
        //check the firerate
        if(target != null && hit) {            
            double rotationToTargetVector = getRotationToEntity(target); 
            
            if(rotationToTargetVector < 0)
                rotationToTargetVector += 360;
            
            double rotation = (rotationToTargetVector - getRotation()) % 360;
            
            double small = Rsmall.evaluate(rotation);
            double medium = Rmedium.evaluate(rotation);
            double large = Rlarge.evaluate(rotation);
            
            left = Rleft.evaluate(rotation);
            facing = Rfacing.evaluate(rotation);
            right = Rright.evaluate(rotation);
            
            fireRate = ((small * 100) + (medium * 10) + (large * 1))/(small + medium + large);
            turnRate = ((left * 75) + (facing * 1) + (right * -75))/(left + facing + right);
        } else {               
            if(rotationToNodeVector < 0)
                rotationToNodeVector += 360;
            
            double rotation = (rotationToNodeVector - getRotation()) % 360;

            left = Rleft.evaluate(rotation);
            facing = Rfacing.evaluate(rotation);
            right = Rright.evaluate(rotation);
            
            if(distance1 < 20){
                left = 0;
                right = 72;
            }
            else if(distance2 < 20){
                left = 70;
                right = 0;
            }
                
                
            fireRate = 0.0;
            turnRate = ((left * 75) + (facing * 1) + (right * -75))/(left + facing + right);   
        }
        if(rhit)
            fireRate = 80.0;
        
        if(Double.isNaN(turnRate))
            turnRate = -50; //Default turn right
        //turnRate += (System.currentTimeMillis() % 2 == 0) ? 10 : -10;
        //System.out.println("turn " + turnRate);
        
        //slow logic------------------------------------------------------------
        //if ray 1 is close and turning left -> slow  
        slow = FuzzyOperator.fuzzyAND(Rclose.evaluate(distance1), left);
        //if ray 2 is close and turning right -> slow
        slow = FuzzyOperator.fuzzyOR(slow, FuzzyOperator.fuzzyAND(Rclose.evaluate(distance2), right));
        //if ray 1 and 2 are close -> slow
        slow = FuzzyOperator.fuzzyOR(slow, FuzzyOperator.fuzzyAND(Rclose.evaluate(distance1), Rclose.evaluate(distance2)));
        //if angle to node is large -> slow
        slow = FuzzyOperator.fuzzyOR(slow, largeAngle);
        //if not facing -> slow
        slow = FuzzyOperator.fuzzyOR(slow, FuzzyOperator.fuzzyNOT(facing));
        
        //normal logic----------------------------------------------------------
        //if ray 1 is middle and turning left -> normal  
        normal = FuzzyOperator.fuzzyAND(Rmiddle.evaluate(distance1), left);
        //if ray 2 is middle and turning right -> normal
        normal = FuzzyOperator.fuzzyAND(normal, FuzzyOperator.fuzzyAND(Rmiddle.evaluate(distance2), right));
        //if ray 1 and 2 are middle -> normal
        normal = FuzzyOperator.fuzzyOR(normal, FuzzyOperator.fuzzyAND(Rmiddle.evaluate(distance1), Rmiddle.evaluate(distance2)));
        //if angle to node is normal -> normal
        normal = FuzzyOperator.fuzzyOR(normal, normalAngle);
        
        //fast logic------------------------------------------------------------
        //if ray 1 and 2 are far -> fast
        fast = FuzzyOperator.fuzzyOR(fast, FuzzyOperator.fuzzyAND(Rfar.evaluate(distance1), Rfar.evaluate(distance2)));
        //if ray 1 and 2 are normal -> fast
        fast = FuzzyOperator.fuzzyOR(fast, FuzzyOperator.fuzzyAND(Rmiddle.evaluate(distance1), Rmiddle.evaluate(distance2)));
        //if is facing -> fast  
        fast = FuzzyOperator.fuzzyAND(fast, facing);
        //if angle to node is small -> fast
        fast = FuzzyOperator.fuzzyOR(fast, smallAngle);
        
        if(fast == 1)
            System.out.println(fast * getWeight3());
        
//        slow = Rslow.evaluate(slow);
//        normal = Rnormal.evaluate(normal);
//        fast = Rfast.evaluate(fast);
       if(Double.isNaN(slow))
           slow = 0.0;
       if(Double.isNaN(normal))
           normal = 0.0;
       if(Double.isNaN(fast))
           fast = 0.0; 
       // double result = ((slow * getWeight()) + (normal * getWeight2()) + (fast * getWeight3()))/(slow + normal + fast);
     //  System.out.println("Emery res: " + result + " \r\n    slow:" + slow + " "  + weight + " \r\n   normal:" + normal + "  " + weight2  +" \r\n   fast:" + fast + " " + weight3); 
       
       double result = 0;
        
        /***
         * Mamdani's method
         */  

        FuzzySet tempa = Rslow.applyImplication(slow *  getWeight()); 
        //medium speed
        //distance medium
        FuzzySet tempb = Rnormal.applyImplication(normal * getWeight2());
        //fast speed
        FuzzySet tempc = Rfast.applyImplication(fast * getWeight3());
        fin = tempc.aggregate(tempa).aggregate(tempb);
        result = fin.defuzzifyRule();
        //if(result >= 80)
        //    System.out.println( this.getIdentifier() + ":" + result);
        if(result < 15)
               result = 15;
        moveRate = result;
    }
    
    @Override public void render(Graphics graphics){
        //used to display where the rays collide
        if(hasPath() && GameState.isShowRay()) {
            Tile tile = path.get(path.size() - 1);
            graphics.setColor(Color.yellow);

            float theta = (float)Math.toRadians(getRotationToEntity(tile));
            float r = (float)getDistanceToEntity(tile) ;
            float x2 = (float)((r * Math.cos(theta)) + this.getX());
            float y2 =(float)((r * Math.sin(theta)) + this.getY());

            graphics.drawLine(this.getX(), this.getY(), x2, y2);
            graphics.fillRect(tile.getX(), tile.getY(), 8, 8);
            
            graphics.setColor(Color.cyan);
            graphics.fillRect(primaryRay.getX(), primaryRay.getY(), 8, 8);
            graphics.drawLine(primaryRay.getOriginx(), primaryRay.getOriginy(), primaryRay.getX(), primaryRay.getY());
            

            graphics.setColor(Color.red);
            graphics.fillRect(secondaryRay.getX(), secondaryRay.getY(), 8, 8);
            graphics.drawLine(secondaryRay.getOriginx(), secondaryRay.getOriginy(), secondaryRay.getX(), secondaryRay.getY());
        }
        
        if(GameState.isShowDirections()){
            graphics.setColor(Color.cyan);

            float theta = (float)((this.getRotation() * Math.PI)/180);
            float r = (float)48.0 ;
            float x2 = (float)( r * Math.cos(theta) + this.getX());
            float y2 =(float)(r * Math.sin(theta) + this.getY());

            graphics.drawLine(this.getX(), this.getY(), x2, y2);
            graphics.drawRect(x2, y2, 3, 3);

            //0.0 degrees
            graphics.setColor(Color.red);
            theta = (float)0.0;
            r = (float)48.0;
            x2 = (float)( r * Math.cos(theta) + this.getX());
            y2 =(float)(r * Math.sin(theta) + this.getY());
            graphics.drawLine(this.getX(), this.getY(), x2, y2);
            graphics.drawRect(x2, y2, 3, 3);
            //90.0 degrees
            graphics.setColor(Color.yellow);
            theta = (float)(0.5 * Math.PI);
            r = (float)48.0;
            x2 = (float)( r * Math.cos(theta) + this.getX());
            y2 =(float)(r * Math.sin(theta) + this.getY());
            graphics.drawLine(this.getX(), this.getY(), x2, y2);
            graphics.drawRect(x2, y2, 3, 3);
            //180.0 degrees
            graphics.setColor(Color.blue);
            theta = (float)Math.PI;
            r = (float)48.0;
            x2 = (float)( r * Math.cos(theta) + this.getX());
            y2 =(float)(r * Math.sin(theta) + this.getY());
            graphics.drawLine(this.getX(), this.getY(), x2, y2);
            graphics.drawRect(x2, y2, 3, 3);
            //270.0 degrees
            graphics.setColor(Color.magenta);
            theta = (float)(1.5 * Math.PI);
            r = (float)48.0;
            x2 = (float)( r * Math.cos(theta) + this.getX());
            y2 =(float)(r * Math.sin(theta) + this.getY());
            graphics.drawLine(this.getX(), this.getY(), x2, y2);
            graphics.drawRect(x2, y2, 3, 3);
        }
 
        graphics.setColor(Color.white);
         DecimalFormat df = new DecimalFormat("#.##");
         if(GameState.isShowName()){
            graphics.drawString(df.format(getMoveRate()) + " ", getX() + 16, getY() + 16);
        }
        super.render(graphics);
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return the weight2
     */
    public double getWeight2() {
        return weight2;
    }

    /**
     * @return the weight3
     */
    public double getWeight3() {
        return weight3;
    }
}