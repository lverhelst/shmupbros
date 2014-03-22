package Game;

import Ai.Ray;
import Game.Entity.Bot;
import Game.Entity.Bot.MODE;
import Game.Entity.Entity;
import Game.Map.Tile;
import Game.State.GameState;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import static org.lwjgl.input.Mouse.getX;
import static org.lwjgl.input.Mouse.getY;

/**
* AIManager
* @author Emery Berg and Leon Verhelst
*/
public class AIManager {
    private ArrayList<Bot> ai;
    private LinkedList<MyThread> threads;
    private Random rand;
    private Ray ray,ray2;
    
    /**
     * The valid move types
     */
    public enum MOVE {
        Up,
        DOWN,
        ROTLEFT,
        ROTRIGHT,
        FIRE,
        RESPAWN,
        SHOWSCORE,
        SHOWNAMES        
    }
    
    /**
     * Default constructor
     */
    public AIManager() {
        ai = new ArrayList();
        threads = new LinkedList();
        rand = new Random();
        ray = new Ray();
        ray2 = new Ray();
    }
    
    /**
     * Used to add bot to the game world
     * @param bot 
     */
    public void addAI(Bot bot) {
        ai.add(bot);
        threads.add(new MyThread(bot));
       // GameState.addEntity(bot);
        //test path for the bots
        for(int i = 0; i < 10; ++i) {
            int x = rand.nextInt(1000) + 32;
            int y = rand.nextInt(1000) + 32;
            Entity node = new Entity(1);
            node.setX(x);
            node.setY(y);
            bot.addPathNode(node);
        }
    }
    
    public Bot getBot(int i){
        return ai.get(i);
    }
    
    /**
     * Update all ai bots
     */
    public void update() {
        for(int i = 0; i < ai.size(); ++i) {
            move(ai.get(i), i);          
        }
    }
    
    /**
     * Let the AI manager choose how to move the bot
     * @param bot the both to move
     */
    public void move(Bot bot, int index) {
        
        int choice = 4;
//        int choice = rand.nextInt(10);        
        
        MODE m = bot.getMode();
        if( m == MODE.AGGRESSIVE){
           
        }
        if(!bot.isAlive()){
            GameState.spawn(bot);
        }
        if(bot.getTarget() == null || !bot.getTarget().isAlive()) {
            bot.chooseRandTarget();
        } 
        
        double dist = bot.getDistanceToEntity(bot.getTarget());
       // if( dist > 0)
         //System.out.println(dist + "  " + FuzzyRule.forceFromDistance(dist));
        
        switch(choice) {
            case 0:                
                Controller.update(bot, Controller.MOVE.UP);
                if(bot.isFacingTarget() == -1)
                    Controller.update(bot, Controller.MOVE.ROTRIGHT);
                else if (bot.isFacingTarget() == 1)
                    Controller.update(bot, Controller.MOVE.ROTLEFT);
                break;
            case 1:
                //zombie
                 bot.faceTarget();
                // bot.applyForce((int)FuzzyRule.forceFromDistance(dist), bot.getRotation());
              // bot.rotateToTarget();
                break;
            case 2:
                //ASTAR PATHFINDING!!!
                if(bot.path2 != null &&  !bot.path2.isEmpty()){
                    Tile t = bot.path2.get(bot.path2.size() - 1);
                    
                    if(rand.nextInt(100) < 100)
                        Controller.update(bot, Controller.MOVE.UP);
                   
                    if(bot.isFacingTile(t) == -1)
                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
                    else if(bot.isFacingTile(t) == 1)
                        Controller.update(bot, Controller.MOVE.ROTLEFT);
                    else
                        Controller.update(bot, Controller.MOVE.FIRE);
                    
                    if(bot.isFacingTarget() == 0)
                        Controller.update(bot, Controller.MOVE.FIRE);
                   
                }else{
                    bot.rotateToTarget();
                    Controller.update(bot, Controller.MOVE.FIRE);
                }
                break;
            case 3:
                if(bot.hasPath()) {
                    Entity node = bot.getPathNode();
                    double distNode = bot.getDistanceToEntity(node);
                    double angleNode = bot.getRotationToEntity(node);
                    
                    Controller.update(bot, Controller.MOVE.UP);
                    
                    if(bot.getRotation() + 2 < angleNode)
                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
                    else if(bot.getRotation() - 2 > angleNode)
                        Controller.update(bot, Controller.MOVE.ROTLEFT);
                    
                    //if at node, get mext node
                    if(distNode < bot.getSize()) 
                        bot.nextPathNode();                    
                }
                break;
            case 4:                
                ray.cast(bot.getX(), bot.getY(), bot.getRotation() + 5, 32, bot.getID());
                ray2.cast(bot.getX(), bot.getY(), bot.getRotation() - 5, 32, bot.getID());

                if(ray.getDistance() < ray2.getDistance()) {
                        Controller.update(bot, Controller.MOVE.ROTRIGHT); 
                    if(ray.getDistance() > 128) {                   
                        Controller.update(bot, Controller.MOVE.UP);
                    } else {                       
                        Controller.update(bot, Controller.MOVE.DOWN);
                    }
                } else {
                        Controller.update(bot, Controller.MOVE.ROTLEFT); 
                    if(ray2.getDistance() > 128) {                   
                        Controller.update(bot, Controller.MOVE.UP);
                    } else {                      
                        Controller.update(bot, Controller.MOVE.DOWN);
                    }
                }
                
                break;
        }

           // Controller.update(bot, Controller.MOVE.FIRE);
           
        //Threading for Path Generation
        //FIFO path finding queue
        if(threads.peek().getState() == Thread.State.NEW)
             threads.peek().start(); 
        else if(threads.peek().getState() == Thread.State.TERMINATED){
            Bot b = threads.pop().bot;
            threads.add(new MyThread(b));
        }
        
             
       //add to end of queue
        
     

        
       //if(rand.nextInt(1000) == 11)
       //   bot.chooseRandTarget();
    }
    
    public class MyThread extends Thread {
        private final Bot bot;
        public long last_locked;
        
        MyThread(final Bot b){
            bot = b;
            this.setName(b.getIdentifier() + " Thread");
        }
        
        @Override public void run(){
               // System.out.println(bot.getIdentifier() + " is running");
                bot.generatePathToTarget();
       }
        
        @Override public String toString(){
            return this.getName();
        }
    }
}