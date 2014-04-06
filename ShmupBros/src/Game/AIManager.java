package Game;

import Game.Entity.Bot;
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
    }
    
    /**
     * Used to add bot to the game world
     * @param bot 
     */
    public void addAI(Bot bot) {
        ai.add(bot);
        threads.add(new MyThread(bot));       
    }
    
    /**
     * Updates the all AI controlled bots
     */
    public void update() {
        for(Bot bot: ai) {
            //spawn if dead
            if(!bot.isAlive() && System.currentTimeMillis() > bot.getDeathTime() + 500){
                GameState.spawn(bot);
            }
            
            //checks the bots values for moving, turning and attacking
            move(bot);
            turn(bot);
            attack(bot);            
            
            //threads used for path generation
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
        
        if(rand.nextDouble() * 100 < speed)
            Controller.update(bot, Controller.MOVE.UP);
    }
    
    /**
     * Used to make the bot turn 
     * @param bot the bot to turn
     */
    public void turn(Bot bot) {
        double speed = bot.getTurnRate();
                        
        if(speed < 15)
            Controller.update(bot, Controller.MOVE.ROTLEFT);
        else if (speed > -15)
            Controller.update(bot, Controller.MOVE.ROTRIGHT);        
    }
    
    /**
     * Used to make the bot choose if they should attack or not
     * @param bot the bot to choose if to attack or not
     */
    public void attack(Bot bot) {
        double speed = bot.getFireRate();        
                 
        if(rand.nextDouble() * 100 < speed)
            Controller.update(bot, Controller.MOVE.FIRE);
    }
    
    /**
     * Inner class for threading the pathing process
     */
    public class MyThread extends Thread {
        private final Bot bot;
        public long last_locked;
        
        /**
         * Default constructor which takes the related bot as a parameter
         * @param b the bot to use
         */
        MyThread(final Bot b){
            bot = b;
            this.setName(b.getIdentifier() + " Thread");
        }
        
        /**
         * Runs the path generation
         */
        @Override public void run(){
                bot.generatePathToTarget();
       }
        
        /**
         * @return the threads name as a String
         */
        @Override public String toString(){
            return this.getName();
        }
    }
}