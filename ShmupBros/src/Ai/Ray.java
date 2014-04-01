package Ai;

import Game.Entity.Entity;
import Game.Entity.Physical;
import Game.State.GameState;

/**
 * Used to cast a ray and find out what it hit and its distance
 * @author Emery
 */
public class Ray extends Physical {
    private float distance, originx, originy;
    private Physical hit;
    private Entity caster;
    
    /**
     * Default constructor
     */
    public Ray() {
        super(16);
        setType(TYPE.RAY);
    }
    
    /**
     * Used to cast a ray from a position at an angle
     * @param angle the angle to move in
     * @param caster the physical entity to cast the ray
     * @param size the size of the ray to cast
     * @return true if the ray collides with an entity, false if not
     */
    public boolean cast(Physical caster, float angle, float size, boolean isLeft) {
        //retrieves the owners position and rotation
        
        double r = -22.62;
        double theta = Math.toRadians(caster.getRotation() + (isLeft? -45:  45));
        float x = (float)(r * Math.cos(theta) + caster.getX());
        float y = (float)(r * Math.sin(theta) + caster.getY());
        originx = x;
        originy = y;
        setX(x); 
        setY(y);
        setRotation(caster.getRotation());
        setSize(size);
        
        //reset the ray properties
        setForceX(0);        
        setForceY(0);
        setCollidable(true);
        hit = null;
        this.caster = caster;
        
        //set and apply the rotation and force
        modRotation(angle);
        applyForce(24, getRotation());
        
        
        //search until a collision happens
        while(getCollidable()) {            
            GameState.checkCollisions(this);
            modX(getForceX());
            modY(getForceY());
        } 
        
        //calculate the distance traveled
        distance = getDistanceToEntity(caster);
        //check if object hit is an Entity
        return hit instanceof Entity; //did not collide with entity
    }
    
    /**
     * Used to cast a ray from a position at an angle
     * @param caster the entity casting the ray
     * @param target the entity for the ray to cast towards
     * @param size the size of the ray to cast
     * @return true if the ray reached target
     */
    public boolean cast(Entity caster, Entity target, float size) {
        //retrieves the owners position and rotation
        setX(caster.getX()); 
        setY(caster.getY());
        setRotation(caster.getRotationToEntity(target));
        
        //reset the ray properties
        setForceX(0);        
        setForceY(0);
        setCollidable(true);
        hit = null;
        this.caster = caster;
        
        //set and apply the force
        applyForce(24, getRotation());        
        
        //search until a collision happens
        while(getCollidable() && distance > 32) {
            GameState.checkCollisions(this);
            modX(getForceX());
            modY(getForceY());
        } 
        
        //calculate the distance traveled
        distance = target.getDistanceToEntity(this);
        
        return distance <= 32; //collided with target
    }
    
    /**
     * @return the object which the ray collided with 
     */
    public Physical getHit() {
        return hit;
    }
    
    /**
     * @return the distance to the object as a float 
     */
    public float getDistance() {
        return distance;
    }
    
    @Override public void Collide() { 
        hit = null;
        setCollidable(false);
    }
    
    /**
     * Base function used when objects collide
     * @param col Physical object which has been collided with
     */
    @Override public void Collide(Physical col) {
        if(col.getID() != caster.getID() && col.getType() != TYPE.PROJECTILE) {
            hit = col;
            setCollidable(false);
        }
    }

    /**
     * @return the originx
     */
    public float getOriginx() {
        return originx;
    }

    /**
     * @return the originy
     */
    public float getOriginy() {
        return originy;
    }
}