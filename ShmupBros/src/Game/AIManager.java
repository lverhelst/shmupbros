package Game;

import Ai.FuzzyRule;
import Game.Bot.MODE;
import Game.Entity.Entity;
import Game.Map.Tile;
import Game.State.GameState;
import java.util.ArrayList;
import java.util.Random;

/**
* AIManager
* @author Emery Berg and Leon Verhelst
*/
public class AIManager {
    private ArrayList<Bot> ai;
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
        rand = new Random();
    }
    
    /**
     * Used to add bot to the game world
     * @param bot 
     */
    public void addAI(Bot bot) {
        ai.add(bot);
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
            move(ai.get(i));          
        }
    }
    
    /**
     * Let the AI manager choose how to move the bot
     * @param bot the both to move
     */
    public void move(Bot bot) {

        int choice = 2;
//        int choice = rand.nextInt(10);        
        
        MODE m = bot.getMode();
        if( m == MODE.AGGRESSIVE){
           
        }
        
        if(!bot.isAlive())
            GameState.spawn(bot);
        
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
                // bot.faceTarget();
                 bot.applyForce((int)FuzzyRule.forceFromDistance(dist), bot.getRotation());
               bot.rotateToTarget();
                break;
            case 2:
                if(bot.path2 != null &&  !bot.path2.isEmpty()){
                    Tile t = bot.path2.get(bot.path2.size() - 1);
                    double distNode = bot.getDistanceToTile(t);
                    double angleNode = bot.getRotationToTile(t);
                    //if at node, get mext node
                    if(distNode < bot.getSize() || bot.path2.size() > 2) {
                       // t = bot.path2.get(bot.path2.size() - 2);
                        double angleNode2 = bot.getRotationToTile(t);
                        //average rotation
                       // angleNode = (angleNode2 + angleNode) / 2;
                        /*if(bot.path2.size() > 3){
                            double angleNode3 = bot.getDistanceToTile(bot.path2.get(bot.path2.size() - 3));
                            angleNode = (angleNode + angleNode2 + angleNode3) /2;
                            
                        }*/
                        
                    }
                    bot.setRotation((float)angleNode);
                    bot.setX(t.getX() + 16);
                    bot.setY(t.getY() + 16);
                    bot.rotateToTarget();
                    //if(bot.isFacingTile(t) == 0)
                  
                        //Controller.update(bot, Controller.MOVE.UP);
                    
                   /* if(bot.isFacingTile(t) == -1)
                       Controller.update(bot, Controller.MOVE.ROTRIGHT);
                    else if(bot.isFacingTile(t) == 1)
                        Controller.update(bot, Controller.MOVE.ROTLEFT);
                    */
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
        }
        if(rand.nextInt(100) <= 111){
           // Controller.update(bot, Controller.MOVE.FIRE);
            bot.generatePathToTarget();
        }
        
       //if(rand.nextInt(1000) == 11)
       //   bot.chooseRandTarget();
       
       
    }    
}