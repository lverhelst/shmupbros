package Game;

import Ai.FuzzyLogic;
import Ai.FuzzyRule;
import Game.Bot.MODE;
import Game.Entity.Projectile;
import Game.State.GameState;
import java.util.ArrayList;
import java.util.Random;

/**
* AIManager
* @author Emery Berg
*/

public class AIManager {
    private ArrayList<Bot> ai;
    private Random rand;
    
    public AIManager() {
        ai = new ArrayList();
        rand = new Random();
    }
    
    public void addAI(Bot bot) {
        ai.add(bot);
       // GameState.addEntity(bot);
    }
    
    public void update() {
        for(int i = 0; i < ai.size(); ++i) {
            move(ai.get(i));          
        }
    }
    
    public void move(Bot bot) {
        int choice = rand.nextInt(10);
        
        MODE m = bot.getMode();
        if( m == MODE.AGGRESSIVE){
           
        }
        
        if(!bot.isAlive())
            GameState.spawn(bot);
        
        if(bot.getTarget() != null && !bot.getTarget().isAlive())
            bot.chooseRandTarget();
        double dist = bot.getDistanceToEntity(bot.getTarget());
       // if( dist > 0)
         //System.out.println(dist + "  " + FuzzyRule.forceFromDistance(dist));
        
        switch(choice) {
            case 0:
                //zombie
               // bot.faceTarget();
                bot.applyForce((int)FuzzyRule.forceFromDistance(dist), bot.getRotation());
                bot.rotateToTarget();
                break;
            case 1:
                //zombie
                // bot.faceTarget();
                 bot.applyForce((int)FuzzyRule.forceFromDistance(dist), bot.getRotation());
               bot.rotateToTarget();
                break;
            case 2:
                //zombie
                bot.applyForce((int)FuzzyRule.forceFromDistance(dist), bot.getRotation());
                break;
            case 3:
               // bot.rotateToTarget();
                 bot.applyForce((int)FuzzyRule.forceFromDistance(dist), bot.getRotation());
                break;
        }
        
        if(rand.nextInt(120) < 20){
            bot.attack(new Projectile(12, bot));
        }
        
       if(rand.nextInt(1000) == 11)
          bot.chooseRandTarget();
    }
}