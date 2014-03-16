package Game;

import Communications.MCManager;
import Game.Entity.Playable;
import Game.State.GameState;

/**
 * Used to control playable entities in the game
 * @author Emery
 */
public class Controller {
    public static boolean showScore;
    public static boolean showNames;
    
    /**
     * The valid move types
     */
    public enum MOVE {
        UP,
        DOWN,
        ROTLEFT,
        ROTRIGHT,
        FIRE,
        RESPAWN,
        SHOWSCORE,
        HIDESCORE, 
        SHOWNAMES        
    }
    
    /**
     * Default update method for controlling the players target and checking for
     * collisions
     * @param move 
     */
    public static void update(Playable target, MOVE move){
        boolean updatedLocation = false;        
        switch(move) {   
            case SHOWSCORE:
                 showScore = true;
                break;         
            case HIDESCORE:
                 showScore = false;
                break;
            case SHOWNAMES:
                target.setShowName(!target.isShowName());
                break;
            case RESPAWN:
                GameState.spawn(target);
                break;
            case UP:
                target.applyForce(target.getSpeed(), target.getRotation());
                updatedLocation = true;
                break;
            case DOWN:
                target.applyForce(-target.getSpeed(), target.getRotation());
                updatedLocation = true;
                break;
            case ROTLEFT:
                target.modRotation(-target.getRotationSpeed());
                updatedLocation = true;
                break;
            case ROTRIGHT:
                target.modRotation(target.getRotationSpeed());
                updatedLocation = true;
                break;
            case FIRE:
                target.attack();    
                break;
        }
        
        //only sends updates if player has moved
        if (updatedLocation & MCManager.getSender() != null) {
            MCManager.getSender().sendPosition(target);
        }
    }
}