package Game.Entity;

import Game.AIManager.MyThread;
import Ai.AStar;
import Ai.FuzzyLogic;
import Ai.Ray;
import Ai.Rule;
import Game.State.GameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import Game.Map.Tile;
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
    private Ray primaryRay, secondaryRay, targetRay;
    private double weight, weight2, weight3;
    private double fireRate, turnRate, moveRate;
        
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
        
        //used to give weights to fuzzy move logic
        weight = 20;
        weight2 = 75;
        weight3 = 100;
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
        primaryRay.cast(this, 10, 8);
        secondaryRay.cast(this, - 10, 8);
        boolean hit = targetRay.cast(this, target, 32);
        
        double distance1 = primaryRay.getDistance();
        double distance2 = secondaryRay.getDistance();
        
        Rule Rclose = GameState.getRule("Close");
        Rule Rmiddle = GameState.getRule("Middle");
        Rule Rfar = GameState.getRule("Far");
        
        Rule Rsmall = GameState.getRule("Small");
        Rule Rmedium = GameState.getRule("Medium");
        Rule Rlarge = GameState.getRule("Large");
        
        Rule Rleft = GameState.getRule("Left");
        Rule Rfacing = GameState.getRule("Facing");
        Rule Rright = GameState.getRule("Right");
        
        //distance infront to colliable
        double close = Rclose.evaluate(distance1);
        double middle = Rmiddle.evaluate(distance1);
        double far = Rfar.evaluate(distance1);
                
        double close2 = Rclose.evaluate(distance2);
        double middle2 = Rmiddle.evaluate(distance2);
        double far2 = Rfar.evaluate(distance2);
                        
        double result = ((close * weight) + (middle * weight2) + (far * weight3))/(close + middle + far);
        double result2 = ((close2 * weight) + (middle2 * weight2) + (far2 * weight3))/(close2 + middle2 + far2);
        
        result = FuzzyLogic.fuzzyAND(result, result2);
            
        double rotationToNodeVector = 0;
        
        //check angle to A* path
        if(hasPath() ) {            
            rotationToNodeVector = getRotationToEntity(path.get(path.size() - 1));
            
            if(path.size() > 2) { //&& bot can see node 2
               rotationToNodeVector = getRotationToEntity(path.get(path.size() - 2));
            }
            
            double rotation = rotationToNodeVector - getRotation() % 180;
            
            double small = Rsmall.evaluate(rotation);
            double medium = Rmedium.evaluate(rotation);
            double large = Rlarge.evaluate(rotation);
            
            double nresult = ((small * weight3) + (medium * weight2) + (large * weight))/(small + medium + large);
            
            result = FuzzyLogic.fuzzyOR(nresult, result); //says if ratation small, go fast  
        }
        
        moveRate = result;
        
        //check the firerate
        if(target != null && hit) {            
            double rotationToTargetVector = getRotationToEntity(target); 
            
            if(rotationToTargetVector < 0)
                rotationToTargetVector += 360;
            
            double rotation = (rotationToTargetVector - getRotation()) % 360;
            
            double small = Rsmall.evaluate(rotation);
            double medium = Rmedium.evaluate(rotation);
            double large = Rlarge.evaluate(rotation);
            
            double left = Rleft.evaluate(rotation);
            double facing = Rfacing.evaluate(rotation);
            double right = Rright.evaluate(rotation);
            
            fireRate = ((small * 100) + (medium * 1) + (large * 1))/(small + medium + large);
            turnRate = ((left * 50) + (facing * 1) + (right * -50))/(left + facing + right);            
        } else {               
            if(rotationToNodeVector < 0)
                rotationToNodeVector += 360;
            
            double rotation = (rotationToNodeVector - getRotation()) % 360;

            double left = Rleft.evaluate(rotation);
            double facing = Rfacing.evaluate(rotation);
            double right = Rright.evaluate(rotation);

            turnRate = ((left * 50) + (facing * 1) + (right * -50))/(left + facing + right);   
        }
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
            graphics.drawLine(getX(), getY(), primaryRay.getX(), primaryRay.getY());

            graphics.setColor(Color.red);
            graphics.fillRect(secondaryRay.getX(), secondaryRay.getY(), 8, 8);
            graphics.drawLine(getX(), getY(), secondaryRay.getX(), secondaryRay.getY());
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
        
        super.render(graphics);
    }
}