package Game.Entity;

import Game.AIManager.MyThread;
import Ai.AStar;
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
    Random rng = new Random();
    
    //fuzzy logic attributes
    private Ray primaryRay, secondaryRay, targetRay, frontRay;
    private double weight, weight2, weight3;
    private double fireRate, turnRate, moveRate, learnRate;
    private double slow, normal, fast, left, facing, right;
    private long lastChoseTarget;
    
    public Bot(float f){
        super(f);
        super.setColor(Color.orange);
        path = new ArrayList<>();
        
        //creates a holder for the A* path
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
        
        //sets the speed which the bot learns at
        learnRate = 8;
        lastChoseTarget = 0;
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
    
    /**
     * Generates a path to the bots target using A* pathing algorithm
     */
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
                   // pathfind   
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
        if(!Settings.survival && lastChoseTarget < System.currentTimeMillis() + 1000) {
            lastChoseTarget = System.currentTimeMillis();
            
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
    
    /**
     * Used to reward and punish the bot if they collide. Values which are high
     * will be punished while low values will be rewarded
     */
    @Override public void Collide() {
        super.Collide();
        
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
        if(!isAlive())
        {
            moveRate =0.0;
            turnRate =0.0;
            fireRate =0.0;
        }
        applyFuzzy();
    }
    
    public void applyFuzzy() {
        //cast the rays to use in fuzzy logic
        primaryRay.cast(this, 5, 8, 1);
        secondaryRay.cast(this, -5, 8, 2);
        boolean rhit = frontRay.cast(this, 1, 8, 0);
        boolean hit = targetRay.cast(this, target, 32);
        
        //ray casts
        double distance1 = primaryRay.getDistance();
        double distance2 = secondaryRay.getDistance();
        
        //LOAD FUZZY SETS FROM GAMESTATE
        //angle
        FuzzySet Rsmall = GameState.getRule("Small");
        FuzzySet Rmedium = GameState.getRule("Medium");
        FuzzySet Rlarge = GameState.getRule("Large");
        
        //direction
        FuzzySet Rleft = GameState.getRule("Left");
        FuzzySet Rfacing = GameState.getRule("Facing");
        FuzzySet Rright = GameState.getRule("Right");
        
        double rotationToNodeVector = 0, rotationToTargetVector = 0;
        double smallAngle = 0, normalAngle = 0, largeAngle = 0;
        
        
        //check the firerate
        //if the bot has a target, and the bot can see it's target
        //then turn to target, try shoot target if facing
        if(target != null && hit) {            
            rotationToTargetVector = getRotationToEntity(target); 
            
            //converts the angle to a usable one
            if(rotationToTargetVector < 0)
                rotationToTargetVector += 360;
            
            //calucates the needed rotation to face target
            rotationToTargetVector = (rotationToTargetVector - getRotation()) % 360;

            //the angle fuzzy set calculations
            smallAngle = Rsmall.evaluate(rotationToTargetVector);
            normalAngle = Rmedium.evaluate(rotationToTargetVector);
            largeAngle = Rlarge.evaluate(rotationToTargetVector);
            
            
            //direction to target
            left = Rleft.evaluate(rotationToTargetVector);
            facing = Rfacing.evaluate(rotationToTargetVector);
            right = Rright.evaluate(rotationToTargetVector);
            
            //set fire rate and turning rate
            //Weighted average defuzzification
            fireRate = ((smallAngle * 100) + (normalAngle * 10) + (largeAngle * 1))/(smallAngle + normalAngle + largeAngle);
           
        } else 
            //pathfind
            //check angle to A* path and evaluted angle speed consideration
          if(hasPath()) {            
            rotationToNodeVector = getRotationToEntity(path.get(path.size() - 1));

            //moves to next node if it exist (provides cleaner movement
            if(path.size() > 2) {
                rotationToNodeVector = (rotationToNodeVector + getRotationToEntity(path.get(path.size() - 2)))/2;

                if(Math.abs(rotationToNodeVector) < 5){
                    path.remove(path.size() - 1);
                    path.remove(path.size() - 2);
                }
            }

            //calculate the value to use with the fuzzy sets
            double rotation = rotationToNodeVector - getRotation() % 180;

            //the angle fuzzy set calculations
            smallAngle = Rsmall.evaluate(rotation);
            normalAngle = Rmedium.evaluate(rotation);
            largeAngle = Rlarge.evaluate(rotation);
            
            //converts the angle to a usable one
            if(rotationToNodeVector < 0)
                rotationToNodeVector += 360;
            
            //calucates the needed rotation to face node
            rotationToNodeVector = (rotationToNodeVector - getRotation()) % 360;

            //direction to node
            left = Rleft.evaluate(rotationToNodeVector);
            facing = Rfacing.evaluate(rotationToNodeVector);
            right = Rright.evaluate(rotationToNodeVector);
          
            //sets fire rate and turning rate
            fireRate = 0.0;
          
        }else{
              //if the bot cannot see it's target and the bot doesn't have a target, or a path to its target
              //choose another target?
              //move randomly?
              //chooseRandTarget();
        }
          
        //Force a large turn if too close to walls
        //Avoids getting stuck on walls
        //This manually forces the fuzzy defuzzification to turn
        //This only happens in the case where the bot is very very very very close to the wall 
        //<20 px, which is les than a single block
        if(distance1 < 20){
                left = 0;
                right = 72;
                
          } else if(distance2 < 20){
                left = 70;
                right = 0;
            }   
        //Weighted average defuzzification
         turnRate = ((left * 75) + (facing * 1) + (right * -75))/(left + facing + right);
        //if ray hit a target fire!!!
        if(rhit)
            fireRate = 80.0;
        
        //when resulting value is NaN use a default value
        if(Double.isNaN(turnRate))
            turnRate = -50; //Default turn right
        
        
        FuzzySet temp, last = null;
        FuzzyRule f;
        
        //move through the rules list contained in the config file and evaluate
        for(int i = 0; i < Settings.rules.size(); i++){
            //initate the rule with needed values
            f = Settings.rules.get(i);
            f.setDistance(distance1, distance2);
            f.setTurn(left, facing, right);
            f.setAngle(smallAngle, normalAngle, largeAngle);
            
            //set the related weights
            if(i == 0)
                f.setWeight(weight);
            else if (i == 1)
                f.setWeight(weight2);
            else
                f.setWeight(weight3);
            
            temp = f.evalRule();
                
            //apply the related rules together
            switch(f.getName()){
                case "Slow": slow = f.getConsequent();
                    break;
                case "Normal": normal = f.getConsequent();
                    break;
                case "Fast" : fast = f.getConsequent();
            }
            
            //aggragate the rules's fuzzy sets together until the last rule is reached
            //this result will be defuzzified
            if(i != 0){
                last = temp.aggregate(last);
            }else{
                last = temp;
            }
        }
     
        double result = 0.0;
        //Defuzzify!
        //this used Centroid Defuzzification
        if(last != null) 
            result = last.defuzzifySet();
        
        moveRate = result;
    }
    
    /**
     * Renders the bot and the related debugging tools which have been added
     * @param graphics the graphics object
     */
    @Override public void render(Graphics graphics){
        //used to display where the rays collide
        if(hasPath() && GameState.isShowRay()) {
            Tile tile = path.get(path.size() - 1);
            graphics.setColor(Color.yellow);

            //caluclate the line positions for the node
            float theta = (float)Math.toRadians(getRotationToEntity(tile));
            float r = (float)getDistanceToEntity(tile) ;
            float x2 = (float)((r * Math.cos(theta)) + this.getX());
            float y2 =(float)((r * Math.sin(theta)) + this.getY());

            //draw the line to the node
            graphics.drawLine(this.getX(), this.getY(), x2, y2);
            graphics.fillRect(tile.getX(), tile.getY(), 8, 8);
                        
            //draw the line to the first ray hit
            graphics.setColor(Color.cyan);
            graphics.fillRect(primaryRay.getX(), primaryRay.getY(), 8, 8);
            graphics.drawLine(primaryRay.getOriginx(), primaryRay.getOriginy(), primaryRay.getX(), primaryRay.getY());
            
            //draw the line to the second ray hit
            graphics.setColor(Color.red);
            graphics.fillRect(secondaryRay.getX(), secondaryRay.getY(), 8, 8);
            graphics.drawLine(secondaryRay.getOriginx(), secondaryRay.getOriginy(), secondaryRay.getX(), secondaryRay.getY());
        }
        
        //old debuging tool, no longer used
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