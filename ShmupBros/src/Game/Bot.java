package Game;

import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.State.GameState;
import java.util.Random;
import org.newdawn.slick.Color;

/**
 *
 * @author Leon Verhelst
 */
public class Bot extends Playable {
    private Playable target;
    
    public Bot(float f){
        super(f);
        super.setColor(Color.orange);
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
    public void faceTarget(){
        //face the target
         this.setRotation(this.getRotationToEntity(target));
    }
    
    public void rotateToTarget()
    {
        float angleToFace = this.getRotationToEntity(target);               
        if((this.getRotation() + 5 < angleToFace || this.getRotation() - 5 > angleToFace)){
            //can rotate 15 degrees at a time
            this.modRotation(angleToFace % 15);
        }
    }
    
    /**
     * Chooses random Entity as target
     */
    public void chooseRandTarget(){
        Random rng = new Random();
        
        int thisInd = GameState.getEntities().indexOf(this);
        int index = thisInd;
        int size = GameState.getEntities().size();
        while(index == thisInd ){
            index = rng.nextInt(size);
            if(!GameState.getEntities().get(index).getType().equals("Playable"))
                index = thisInd;
        }
        target = (Playable)GameState.getEntities().get(index);
    }
}
