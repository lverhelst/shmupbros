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
    private ArrayList<Playable> ai;
    private Random rand;
    
    public AIManager() {
        ai = new ArrayList();
        rand = new Random();
    }
    
    public void addAI(Playable bot) {
        ai.add(bot);
        GameState.addEntity(bot);
    }
    
    public void update() {
        int count = GameState.getEntities().size();
        for(int i = 0; i < ai.size(); ++i) {
            move(ai.get(i));
            for(Physical e : GameState.getEntities()){
                
                if(e != ai.get(i) && !e.getType().equals("Projectile")){
                    System.out.println("Distance from " + ai.get(i).getIdentifier() + " to " + e.getType() + " " + e.getIdentifier() + ":" + ai.get(i).getDistanceToEntity(e));
                    System.out.println("Rotation from " + ai.get(i).getIdentifier() + " to " + e.getType() + " " + e.getIdentifier() + ":" + ai.get(i).getRotationToEntity(e));
                }
            }
            
        }
    }
    
    public void move(Playable bot) {
        int choice = rand.nextInt(4);
        
        if(!bot.isAlive())
            GameState.spawn(bot);
        
        switch(choice) {
            case 0:
                bot.applyForce(2, bot.getRotation());
                break;
            case 1:
                bot.applyForce(2, bot.getRotation());
                break;
            case 2:
                bot.modRotation(-10);
                break;
            case 3:
                bot.modRotation(10);
                break;
        }
        
        if(rand.nextInt(20) == 10)
            bot.attack(new Projectile(32, bot));
    }
}