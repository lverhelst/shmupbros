package Game;

import Game.Entity.Playable;

/**
 *
 * @author Leon Verhelst
 */
public class Bot extends Playable {
    private Playable target;
    
    public Bot(float f){
        super(f);
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
}
