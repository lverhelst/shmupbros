package Game;

import Game.Bot.MODE;
import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.Entity.Projectile;
import Game.State.GameState;
import java.util.ArrayList;
import java.util.Random;
import org.newdawn.slick.Input;

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
        GameState.addEntity(bot);
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
        
        switch(choice) {
            case 0:
                //zombie
                bot.faceTarget();

                bot.rotateToTarget();
                bot.applyForce(2, bot.getRotation());
                break;
            case 1:
                //zombie
                bot.rotateToTarget();
                bot.applyForce(1, bot.getRotation());
                break;
            case 2:
                //zombie
                bot.applyForce(1, bot.getRotation());
                break;
            case 3:
               // bot.rotateToTarget();
                bot.applyForce(3, bot.getRotation());
                break;
        }
        
        if(rand.nextInt(120) < 3){
            bot.attack(new Projectile(12, bot));
        }
        
        if(rand.nextInt(500) == 1)
            bot.chooseRandTarget();
    }
}