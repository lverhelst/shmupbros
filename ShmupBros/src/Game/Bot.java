package Game;

import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.State.GameState;
import java.util.Random;

/**
 *
 * @author Leon Verhelst
 */
public class Bot extends Playable {
    private Physical target;
    
    public Bot(float f){
        super(f);
    }

    /**
     * @return the target
     */
    public Physical getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Physical target) {
        this.target = target;
    }
    /**
     * Fact the target
     */
    public void faceTarget(){
        //face the target
         this.setRotation(this.getRotationToEntity(target));
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
            if(GameState.getEntities().get(index).getType().equals("Projectile"))
                index = thisInd;
        }
        target = GameState.getEntities().get(index);
    }
}
