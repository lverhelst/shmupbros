package Game;

import Ai.FuzzyLogic;
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
        //cast ray for simulate fuzzy selection
        rayf.cast(bot, bot.getRotation() + 10, 16);
        raye.cast(bot, bot.getRotation() - 10, 16);
        double distance1, distance2, ndistance;
        double close1, middle1, far1, close2, middle2, far2, nclose, nmiddle, nfar, result1, result2, nresult;
        
        distance1 = rayf.getDistance()/32;
        distance2 = raye.getDistance()/32;
        ndistance = bot.distanceToNode()/32;
        
        //distance infront to colliable
        close1 = FuzzyRule.fuzzyCLOSE(distance1);
        middle1 = FuzzyRule.fuzzyMIDDLE(distance1);
        far1 = FuzzyRule.fuzzyFAR(distance1);
        
        //distance to next node
        close2 = FuzzyRule.fuzzyCLOSE(distance2);
        middle2 = FuzzyRule.fuzzyMIDDLE(distance2);
        far2 = FuzzyRule.fuzzyFAR(distance2);
        
        result1 = (((close1 * 100) + (middle1 * 75) + (far1 * 10))/(close1 + middle1 + far1));
        result2 = (((close2 * 100) + (middle2 * 75) + (far2 * 10))/(close2 + middle2 + far2));
        
        
        
        if(bot.hasPath() ){
            nclose = FuzzyRule.fuzzyCLOSE(ndistance);
            nmiddle = FuzzyRule.fuzzyMIDDLE(ndistance);
            nfar = FuzzyRule.fuzzyFAR(ndistance); 
            float rotationToNodeVector = bot.getRotationToTile(bot.path.get(0));
            //System.out.print(rotationToNodeVector + " ");
            if(bot.path.size() > 2) //&& bot can see node 2
            {
               rotationToNodeVector = (rotationToNodeVector + bot.getRotationToTile(bot.path.get(1)))/2;
            }
            //System.out.print(rotationToNodeVector);
            float rotation = rotationToNodeVector - bot.getRotation() % 180;
            //System.out.println(" needed: " + rotation);
            
            
           // nresult = (((nclose * 100) + (nmiddle * 75) + (nfar * 10))/(nclose + nmiddle + nfar));
            nresult = FuzzyRule.fuzzyRotation(rotation);
            
            
            //nresult = FuzzyRule.fuzzyFACING(rotation);
           //as facing -> 1, speed -> 1 (if facing, go fast)
           
            
            // System.out.println(nresult);
           
            result1 = nresult; //says if ratation small, go fast
           // result1 = FuzzyLogic.fuzzyOR(FuzzyLogic.fuzzyOR(result1, result2), nresult);
            
            
        } else {
            result1 = FuzzyLogic.fuzzyOR(result1, result2);
        }
        
        //apply speed rule
        
        System.out.println(result1);
        if(bot.isFacingTarget() == 0)
            Controller.update(bot, Controller.MOVE.UP);
        
        //if result1 (speed) -> 1.0, up is done more often
        else if(rand.nextDouble() < result1)
            Controller.update(bot, Controller.MOVE.UP);
       // else 
       //     System.err.println(result1 + ":" + close1 + "," + middle1 + "," + far1);
           
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
            rayhit = raye.cast(bot, angleNode, 16);
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
                 if(rayhit && (raye.getHit() instanceof Playable) && ((Playable)raye.getHit()).getID() == bot.getTarget().getID()) {
                     if(bot.isFacingTarget() == -1)
                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
                    else if(bot.isFacingTarget() == 1)
                        Controller.update(bot, Controller.MOVE.ROTLEFT);
                }  else /* Pathfind */ if(bot.path != null &&  !bot.path.isEmpty()){
                    Tile t = bot.path.get(bot.path.size() - 1);           
                    if(bot.isFacingTile(t) == -1)
                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
                    else if(bot.isFacingTile(t) == 1)
                        Controller.update(bot, Controller.MOVE.ROTLEFT);

                    if((Math.abs(bot.getRotationToTile(t) - bot.getRotation()) % 180 < 20) && bot.isFacingTile(t) != 0){
                       // bot.setMoveMode(MODE.PASSIVE);
                    }
                }                
               
                break;
            case STUCK:
                break;
            case DEAD:
                break;
            case RANDOM:
                rayf.cast(bot, bot.getRotation() + 5, 32);
                raye.cast(bot, bot.getRotation() - 5, 32);
                
                if(raye.getDistance() > rayf.getDistance()) 
                    Controller.update(bot, Controller.MOVE.ROTRIGHT);                     
                else 
                    Controller.update(bot, Controller.MOVE.ROTLEFT);                
                break;
            case ZOMBIE:
               
//                bot.faceTarget();
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
       // if(bot.getTarget() == null || !bot.getTarget().isAlive()) 
       //     bot.chooseRandTarget();
        
        double dist = bot.getDistanceToEntity(bot.getTarget());
        
        switch(bot.getAttackMode()) {
            case AGGRESSIVE:
                Controller.update(bot, Controller.MOVE.FIRE);
                break;
            case PASSIVE:
                bot.chooseRandTarget();
                break;
            case SEARCH:
                if(FuzzyRule.fuzzyFACING((bot.getRotationToEntity(bot.getTarget()) - bot.getRotation()) % 180) > 0.95)
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