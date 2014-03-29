package Game;

import Ai.FuzzyRule;
import Ai.Ray;
import Game.Entity.Bot;
import Game.Entity.Bot.MODE;
import Game.Entity.Entity;
import Game.Entity.Playable;
import Game.Map.Tile;
import Game.State.GameState;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
* AIManager
* @author Emery Berg and Leon Verhelst
*/
public class AIManager {
    private ArrayList<Bot> ai;
    private LinkedList<MyThread> threads;
    private Random rand;
    private Ray raye,rayf;
    
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
        raye = new Ray();
        rayf = new Ray();
    }
    
    /**
     * Used to add bot to the game world
     * @param bot 
     */
    public void addAI(Bot bot) {
        ai.add(bot);
        threads.add(new MyThread(bot));       
    }
    
    public Bot getBot(int i){
        return ai.get(i);
    }
    
    /**
     * Updates the all AI controlled bots
     */
    public void update() {
        for(Bot bot: ai) {
            //spawn if dead
            if(!bot.isAlive()){
                GameState.spawn(bot);
                bot.setAllMode(MODE.SEARCH);
            }
            move(bot);
            turn(bot);
            attack(bot);
            
            if(threads.peek().getState() == Thread.State.NEW)
                 threads.peek().start(); 
            else if(threads.peek().getState() == Thread.State.TERMINATED){
                Bot b = threads.pop().bot;
                threads.add(new MyThread(b));
            }
        }
    }
    
    /**
     * Let the AI manager choose how to move the bot
     * @param bot the both to move
     */
    public void move(Bot bot) {
        double speed = bot.getMoveRate();
        
        //apply speed rule
        System.out.println(speed);
//        if(bot.isFacingTarget() == 0)
//            Controller.update(bot, Controller.MOVE.UP);
        
        //if result1 (speed) -> 1.0, up is done more often
        if(rand.nextDouble() * 100 < speed)
            Controller.update(bot, Controller.MOVE.UP);
    }
    
    /**
     * Used to make the bot turn 
     * @param bot the bot to turn
     */
    public void turn(Bot bot) {
        double speed = bot.getTurnRate();
        
//        System.out.println(speed);
        
        if(speed < 10)
            Controller.update(bot, Controller.MOVE.ROTLEFT);
        else if (speed > -10)
            Controller.update(bot, Controller.MOVE.ROTRIGHT);
        
        
//        boolean rayhit = false;
//        float angleNode = 0;
//        
//        if(bot.getTarget() != null && bot.getTarget().isAlive()) {
//            angleNode = bot.getRotationToEntity(bot.getTarget());
//            rayhit = raye.cast(bot, angleNode, bot.getSize());
//        } else {
//            //roam, just does not do it now...
//            bot.setTurnMode(MODE.SEARCH); 
//        }        
//        
//        switch(bot.getTurnMode()) {
//            case AGGRESSIVE:
//                if(bot.getRotation() + 2 < angleNode)
//                   Controller.update(bot, Controller.MOVE.ROTRIGHT);
//                else if(bot.getRotation() - 2 > angleNode)
//                    Controller.update(bot, Controller.MOVE.ROTLEFT);
//                
//                //if no line of sight pathfind
//                if(!rayhit || ((Entity)raye.getHit()) != bot.getTarget()) {
//                    bot.setTurnMode(MODE.SEARCH);
//                }  
//                break;
//            case PASSIVE:
//                break;
//            case SEARCH:
//                //if raycast to target hits target && facing target, zombie mode
//                //else follow path
//                 if(rayhit && (raye.getHit() instanceof Playable) && ((Playable)raye.getHit()).getID() == bot.getTarget().getID()) {
//                     if(bot.isFacingTarget() == -1)
//                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
//                    else if(bot.isFacingTarget() == 1)
//                        Controller.update(bot, Controller.MOVE.ROTLEFT);
//                }  else /* Pathfind */ 
//                     if(bot.path != null &&  !bot.path.isEmpty()){
//                         Tile t;
//                         
//                         if(bot.path.size() > 2) {
//                             t = bot.path.get(bot.path.size() - 2);
//                         } else {
//                             t = bot.path.get(bot.path.size() - 1);
//                         }
//                    
//                    if(bot.isFacingTile(t) == -1)
//                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
//                    else if(bot.isFacingTile(t) == 1)
//                        Controller.update(bot, Controller.MOVE.ROTLEFT);
//
//                    if((Math.abs(bot.getRotationToEntity(t) - bot.getRotation()) % 180 < 20) && bot.isFacingTile(t) != 0){
//                       // bot.setMoveMode(MODE.PASSIVE);
//                    }
//                }                
//               
//                break;
//            case STUCK:
//                break;
//            case DEAD:
//                break;
//            case RANDOM:
//                rayf.cast(bot, bot.getRotation() + 5, 8);
//                raye.cast(bot, bot.getRotation() - 5, 8);
//                
//                if(raye.getDistance() > rayf.getDistance()) 
//                    Controller.update(bot, Controller.MOVE.ROTRIGHT);                     
//                else 
//                    Controller.update(bot, Controller.MOVE.ROTLEFT);                
//                break;
//            case ZOMBIE:
//               
////                bot.faceTarget();
//                 if(!(rayhit && (raye.getHit() instanceof Playable) && ((Playable)raye.getHit()).getID() == bot.getTarget().getID()) || raye.getDistance() > 200) {
//                    bot.setTurnMode(MODE.SEARCH);                 
//                }  
//                break;
//            default:
//                break;
//        }
    }
    
    /**
     * Used to make the bot choose if they should attack or not
     * @param bot the bot to choose if to attack or not
     */
    public void attack(Bot bot) {
        double speed = bot.getFireRate();
        
//        System.out.println(speed);
                
        //if result1 (speed) -> 1.0, up is done more often
        if(rand.nextDouble() * 100 < speed)
            Controller.update(bot, Controller.MOVE.FIRE);
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