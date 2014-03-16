package Game;

import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.State.GameState;
import java.util.Random;
import org.newdawn.slick.Color;
import Game.Entity.Entity.TYPE;

/**
 *
 * @author Leon Verhelst
 */
public class Bot extends Playable {
    private Playable target;
    private MODE mode;    
    
    public enum MODE{
        AGGRESSIVE, //HUNT AND KILL
        PASSIVE, //STAY STILL
        SEARCH, //AQUIRE TARGET
        STUCK, //PATHFIND AROUND OBSTACLE OR SUICIDE AND RESPAWN
        DEAD, //DEAD
        RANDOM //RANDOM MOVES
    }
        
    public Bot(float f){
        super(f);
        super.setColor(Color.orange);
    }
    
    /**
     * @return the mode
     */
    public MODE getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(MODE mode) {
        this.mode = mode;
    }    

    /**
     * @return the target
     */
    public Playable getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Playable target) {
        this.target = target;
    }
    
    /**
     * Fact the target
     */
    public void faceTarget() {        
        //face the target
         this.setRotation(this.getRotationToEntity(target));
    }
    
    /**
     * Method to rotate bot to face its target (for basic AI, fuzzy will use RotationToEntity)
     */
    public void rotateToTarget() {
        float angleToFace = this.getRotationToEntity(target);               
        if((this.getRotation() + 5 < angleToFace || this.getRotation() - 5 > angleToFace)){
            //can rotate 15 degrees at a time
            this.modRotation(angleToFace % 15);
        }
    }
    
    /**
     * Method to return if the bot is facing its target (for basic AI, fuzzy will use RotationToEntity)
     * @return 0 if facing, -1 or 1 if not
     */
    public int isFacingTarget() {
        float angleToFace = getRotationToEntity(target);               
        if(getRotation() + 2 < angleToFace)
            return -1;
        if(getRotation() - 2 > angleToFace)
            return 1;
        
        return 0;
    }
    
    /**
     * Chooses random Entity as target
     */
    public void chooseRandTarget() {
        Random rng = new Random();
        int size = GameState.getEntities().size();
        int answer = rng.nextInt(size);
        
        //Will find a playable object which is alive randomly
        for(Physical p : GameState.getEntities()){
            if(p.getType() == TYPE.PLAYABLE && ((Playable)p).isAlive() && rng.nextInt(size) == answer)
                target = (Playable)p;
        }
        
//        Random rng = new Random();
//        int i = 0; 
//        for(Physical p : GameState.getEntities()){
//            if(p.getType() == TYPE.PLAYABLE)
//                i++;
//        }
//        //Ensure there are entities to choose from
//        if(i > 1){
//            int thisInd = GameState.getEntities().indexOf(this);
//            int index = thisInd;
//            int size = GameState.getEntities().size();
//            while(index == thisInd ){
//                index = rng.nextInt(size);
//                if(!(GameState.getEntities().get(index).getType() == TYPE.PLAYABLE))
//                    index = thisInd;
//            }
//            target = (Playable)GameState.getEntities().get(index);
//        }
    }    
}
