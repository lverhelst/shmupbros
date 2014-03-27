package Game.Entity;

import Game.AIManager.MyThread;
import Ai.AStar;
import Ai.Ray;
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
    private MODE turnMode; 
    private MODE moveMode; 
    private MODE attackMode; 
    private static AStar astar;
    private static Lock lock = new ReentrantLock();
    
    private Ray primaryRay, secondaryRay;
    
    /*
    We might want to change these to float values, that way we can have a mixed mode
    Not 100% sure about that yet, but could allow combo modes
    */
    public enum MODE{
        AGGRESSIVE, //HUNT AND KILL
        PASSIVE, //STAY STILL
        SEARCH, //AQUIRE TARGET
        STUCK, //PATHFIND AROUND OBSTACLE OR SUICIDE AND RESPAWN
        DEAD, //DEAD
        RANDOM, //RANDOM MOVES
        ZOMBIE  //Move in straight lines
    }
        
    public Bot(float f){
        super(f);
        super.setColor(Color.orange);
        path = new ArrayList<>();
        
        if(astar == null)
            astar = new AStar(); 
        
        turnMode = MODE.SEARCH;
        moveMode = MODE.SEARCH;
        attackMode = MODE.SEARCH;        
        
        primaryRay = new Ray();
        secondaryRay = new Ray();
    }
    
    /**
     * @return the turn mode
     */
    public MODE getTurnMode() { return turnMode; }
    
    /**
     * @return the move mode
     */
    public MODE getMoveMode() { return moveMode; }
    
    /**
     * @return the attack mode
     */
    public MODE getAttackMode() { return attackMode; }

    /**
     * @param mode the mode to set all modes to
     */
    public void setAllMode(MODE mode) { 
        this.turnMode = mode;
        this.moveMode = mode;
        this.attackMode = mode; 
    }
    
    /**
     * @param mode the mode to set turn mode to
     */
    public void setTurnMode(MODE mode) { this.turnMode = mode; }
    
    /**
     * @param mode the mode to set move mode to
     */
    public void setMoveMode(MODE mode) { this.moveMode = mode; }
    
    /**
     * @param mode the mode to set attack mode to
     */
    public void setAttackMode(MODE mode) { this.attackMode = mode; }
    
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
        //generatePathToTarget();
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
                    //System.out.println(Thread.currentThread().getName() + " obtained Lock.");         
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
     * Fact the target
     */
    public void faceTarget() {        
        //face the target
         this.setRotation(this.getRotationToEntity(target));
    }
    
    /**
     * Method to rotate bot to face its target (for basic AI, fuzzy will use RotationToEntity)
     */
    public void rotateToTarget() {
        float angleToFace = this.getRotationToEntity(target);               
        if((this.getRotation() + 5 < angleToFace || this.getRotation() - 5 > angleToFace)){
            //can rotate 15 degrees at a time
            this.modRotation(angleToFace % 15);
        }
    }
    
    public void rotateToAngle(float angle){
         if((this.getRotation() + 5 < angle || this.getRotation() - 5 > angle)){
            //can rotate 15 degrees at a time
            this.modRotation(angle % 15);
        }
    }
    
    /**
     * Method to return if the bot is facing its target (for basic AI, fuzzy will use RotationToEntity)
     * @return 0 if facing, -1 or 1 if not
     */
    public int isFacingTarget() {
        float angleToFace = getRotationToEntity(target) ;               
        float rotationNeeded = getRotation() - angleToFace;
        
        if(Math.abs(rotationNeeded) > 180)
            rotationNeeded += rotationNeeded > 0 ? -360 : 360;
 
        if(rotationNeeded + 2 < 0)
            return -1;
        else if(rotationNeeded - 2 > 0)
            return 1;
        return 0;
    }
    
    public int isFacingTile(Tile t){
        float angleToFace = getRotationToTile(t) ;               
        float rotationNeeded = getRotation() - angleToFace;
        
        if(Math.abs(rotationNeeded) > 180)
            rotationNeeded += rotationNeeded > 0 ? -360 : 360;
 
        if(rotationNeeded + 2 < 0)
            return -1;
        else if(rotationNeeded - 2 > 0)
            return 1;
        
       /* if((getRotation() * Math.PI /180) + 2 * Math.PI/180 < angleToFace)
            return -1;
        if((getRotation()  * Math.PI /180) -  2 * Math.PI/180  > angleToFace)
            return 1;
        */
        return 0;
    }
    
    /**
     * Used to check if the bot currently has a path
     * @return true if a path exists
     */
    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }
    
    /**
     * Used to calculate the distance to the next node
     * @return the distance 
     */
     public double distanceToNode(){
         if(hasPath()) {
            Tile node = path.get(0);
            
            double x, y;
            x = getX() - node.getX();
            y = getY() - node.getY();
            
            return Math.sqrt((x * x) + (y * y));
         }
         
        return 0;
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
    
    @Override public void update() {
        super.update();        
        
        //cast the rays to use in fuzzy logic
        primaryRay.cast(this, 10);
        secondaryRay.cast(this, - 10);
    }
    
    @Override public void render(Graphics graphics){
        //used to display where the rays collide
        if(true) {
            graphics.setColor(Color.cyan);
            graphics.fillRect(primaryRay.getX(), primaryRay.getY(), 8, 8);

            graphics.setColor(Color.red);
            graphics.fillRect(secondaryRay.getX(), secondaryRay.getY(), 8, 8);
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
         if(GameState.isShowName())
            graphics.drawString("AttackMode: "  + this.attackMode.toString() + "\r\nMoveMode: " + this.moveMode.toString() + "\r\nTurnMode: " + this.turnMode.toString(), getX(), getY() - (float)6.0 * getSize());
        
        
        super.render(graphics);
    }
}