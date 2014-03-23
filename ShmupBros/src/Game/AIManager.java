package Game;

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
        //cast ray for simulate fuzzy selection
        rayf.cast(bot.getX(), bot.getY(), bot.getRotation(), 16, bot.getID());
                
        switch(bot.getMoveMode()) {
            case AGGRESSIVE:
                if(rand.nextInt(100) < 100)
                    Controller.update(bot, Controller.MOVE.UP);
                break;
            case ZOMBIE: 
                if(rand.nextInt(100) < 90)
                    Controller.update(bot, Controller.MOVE.UP);
                
                if(rayf.getHit() instanceof Playable){
                    if(  ((Playable)rayf.getHit()).getID() == bot.getTarget().getID() ) {
                        bot.setTurnMode(MODE.SEARCH);      
                        bot.setMoveMode(MODE.SEARCH);
                    }
                }
                break;
            case SEARCH: 
                Controller.update(bot, Controller.MOVE.UP);
                /*
                if(rayf.getDistance() < 128) {                    
                    Controller.update(bot, Controller.MOVE.DOWN);
                } else if (rayf.getDistance() < 512) {  
                    if(rand.nextInt(10) < 1)
                        Controller.update(bot, Controller.MOVE.UP);
                } else if (rayf.getDistance() < 1024) {  
                    if(rand.nextInt(10) < 7)
                        Controller.update(bot, Controller.MOVE.UP);
                } else {                    
                    Controller.update(bot, Controller.MOVE.UP);
                }*/
                break;
            case STUCK:
                break;
            case PASSIVE:
                if(bot.path2 != null &&  !bot.path2.isEmpty()){
                    Tile t = bot.path2.get(bot.path2.size() - 1); 
                    if((Math.abs(bot.getRotationToTile(t) - bot.getRotation()) % 180 < 25) || bot.isFacingTile(t) == 0)
                            bot.setMoveMode(MODE.SEARCH);
                } 
               break;
            case RANDOM:                
                if(rayf.getDistance() > 128 && rand.nextInt(100) < 100)
                    Controller.update(bot, Controller.MOVE.UP);
                break;
            case DEAD:
                break;
        }
    }
    
    /**
     * Used to make the bot turn 
     * @param bot the bot to turn
     */
    public void turn(Bot bot) {
        boolean rayhit = false;
        float angleNode = 0;
        
        if(bot.getTarget() != null && bot.getTarget().isAlive()) {
            angleNode = bot.getRotationToEntity(bot.getTarget());
            rayhit = raye.cast(bot.getX(), bot.getY(), angleNode, 16, bot.getID());
        } else {
            //roam, just does not do it now...
            bot.setTurnMode(MODE.SEARCH); 
        }        
        
        switch(bot.getTurnMode()) {
            case AGGRESSIVE:
                if(bot.getRotation() + 2 < angleNode)
                   Controller.update(bot, Controller.MOVE.ROTRIGHT);
                else if(bot.getRotation() - 2 > angleNode)
                    Controller.update(bot, Controller.MOVE.ROTLEFT);
                
                //if no line of sight pathfind
                if(!rayhit || ((Entity)raye.getHit()) != bot.getTarget()) {
                    bot.setTurnMode(MODE.SEARCH);
                }  
                break;
            case PASSIVE:
                break;
            case SEARCH:
                //if raycast to target hits target && facing target, zombie mode
                //else follow path
                //ASTAR PATHFINDING!!!
                if(bot.path2 != null &&  !bot.path2.isEmpty()){
                    Tile t = bot.path2.get(bot.path2.size() - 1);           
                    if(bot.isFacingTile(t) == -1)
                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
                    else if(bot.isFacingTile(t) == 1)
                        Controller.update(bot, Controller.MOVE.ROTLEFT);

                    if((Math.abs(bot.getRotationToTile(t) - bot.getRotation()) % 180 < 20) && bot.isFacingTile(t) != 0){
                        bot.setMoveMode(MODE.PASSIVE);
                    }
                    
                }                
                
                if(rayhit && (raye.getHit() instanceof Playable) && ((Playable)raye.getHit()).getID() == bot.getTarget().getID()) {
                    bot.setTurnMode(MODE.ZOMBIE);      
                    bot.setMoveMode(MODE.ZOMBIE);
                }  
                break;
            case STUCK:
                break;
            case DEAD:
                break;
            case RANDOM:
                rayf.cast(bot.getX(), bot.getY(), bot.getRotation() + 5, 32, bot.getID());
                raye.cast(bot.getX(), bot.getY(), bot.getRotation() - 5, 32, bot.getID());
                
                if(raye.getDistance() > rayf.getDistance()) 
                    Controller.update(bot, Controller.MOVE.ROTRIGHT);                     
                else 
                    Controller.update(bot, Controller.MOVE.ROTLEFT);                
                break;
            case ZOMBIE:
                /*if(bot.isFacingTarget() == -1)
                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
                    else if(bot.isFacingTarget() == 1)
                        Controller.update(bot, Controller.MOVE.ROTLEFT);*/
                bot.faceTarget();
                 if(!(rayhit && (raye.getHit() instanceof Playable) && ((Playable)raye.getHit()).getID() == bot.getTarget().getID()) || raye.getDistance() > 200) {
                    bot.setTurnMode(MODE.SEARCH);                 
                }  
                break;
            default:
                break;
        }
    }
    
    /**
     * Used to make the bot choose if they should attack or not
     * @param bot the bot to choose if to attack or not
     */
    public void attack(Bot bot) {
        //[TODO] need to make it so bot does not require a target at all times
        if(bot.getTarget() == null || !bot.getTarget().isAlive()) 
            bot.chooseRandTarget();
        
        double dist = bot.getDistanceToEntity(bot.getTarget());
        
        switch(bot.getAttackMode()) {
            case AGGRESSIVE:
                Controller.update(bot, Controller.MOVE.FIRE);
                break;
            case PASSIVE:
                bot.chooseRandTarget();
                break;
            case SEARCH:
                if(bot.isFacingTarget() == 0)
                    Controller.update(bot, Controller.MOVE.FIRE);
                break;
            case STUCK:
                break;
            case DEAD:
                break;
            case RANDOM:
                break;
            case ZOMBIE:
                if(bot.isFacingTarget() == 0)
                    Controller.update(bot, Controller.MOVE.FIRE);
                break;
            default:
                break;
        }
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