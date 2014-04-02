package Game.Entity;

import Game.AIManager.MyThread;
import Ai.AStar;
import Ai.FuzzyOperator;
import Ai.FuzzyRule;
import Ai.Ray;
import Ai.FuzzySet;
import Game.State.GameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import Game.Map.Tile;
import Game.Settings;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        weight2 = 0.5;
        weight3 = 0.25;
        
        learnRate = 8;
        
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
        if(!Settings.survival) {
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
        
        double rotationToNodeVector = 0, rotationToTargetVector = 0;
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
            rotationToTargetVector = getRotationToEntity(target); 
            
            if(rotationToTargetVector < 0)
                rotationToTargetVector += 360;
            
             rotationToTargetVector = (rotationToTargetVector - getRotation()) % 360;
            
            double small = Rsmall.evaluate(rotationToTargetVector);
            double medium = Rmedium.evaluate(rotationToTargetVector);
            double large = Rlarge.evaluate(rotationToTargetVector);
            
            left = Rleft.evaluate(rotationToTargetVector);
            facing = Rfacing.evaluate(rotationToTargetVector);
            right = Rright.evaluate(rotationToTargetVector);
            
            fireRate = ((small * 100) + (medium * 10) + (large * 1))/(small + medium + large);
            turnRate = ((left * 75) + (facing * 1) + (right * -75))/(left + facing + right);
        } else {               
            if(rotationToNodeVector < 0)
                rotationToNodeVector += 360;
            
            rotationToNodeVector = (rotationToNodeVector - getRotation()) % 360;

            left = Rleft.evaluate(rotationToNodeVector);
            facing = Rfacing.evaluate(rotationToNodeVector);
            right = Rright.evaluate(rotationToNodeVector);
            
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
        
        
        FuzzySet temp = null, last = null;
        FuzzyRule f;
        for(int i = 0; i < Settings.rules.size(); i++){
            f = Settings.rules.get(i);
            f.setDistance(distance1, distance2);
            f.setTurn(left, facing, right);
            f.setAngle(smallAngle, normalAngle, largeAngle);
            if(i == 0)
                f.setWeight(weight);
            else if (i == 1)
                f.setWeight(weight2);
            else
                f.setWeight(weight3);
            
            temp = f.evalRule();
                
            switch(f.getName()){
                case "Slow": slow = f.getConsequent();
                    break;
                case "Normal": normal = f.getConsequent();
                    break;
                case "Fast" : fast = f.getConsequent();
            }
            if(i != 0){
                last = temp.aggregate(last);
            }else{
                last = temp;
            }
        }
     
        double result = 0.0;
        //Defuzzify!
        if(last != null) 
            result = last.defuzzifyRule();
        
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