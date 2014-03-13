package Game;

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
        
        if(!bot.isAlive())
            GameState.spawn(bot);
        
        switch(choice) {
            case 0:
                //zombie
                //bot.faceTarget();
                break;
            case 1:
                //zombie
                //bot.faceTarget();
                bot.rotateToTarget();
                break;
            case 2:
                bot.applyForce(1, bot.getRotation());
                break;
            case 3:
                bot.applyForce(1, bot.getRotation());
                break;
        }
        
        if(rand.nextInt(200) == 10){
            bot.attack(new Projectile(12, bot));
        }
        
        if(rand.nextInt(100) == 101)
            bot.chooseRandTarget();
    }
}