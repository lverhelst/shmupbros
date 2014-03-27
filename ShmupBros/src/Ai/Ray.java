package Ai;

import Game.Entity.Entity;
import Game.Entity.Physical;
import Game.State.GameState;

/**
 * Used to cast a ray and find out what it hit and its distance
 * @author Emery
 */
public class Ray extends Physical {
    private float distance;
    private Physical hit;
    private Physical target;
    
    /**
     * Default constructor
     */
    public Ray() {
        super(16);
    }
    
    /**
     * Used to cast a ray from a position at an angle
     * @param angle the angle to move in
     * @param ent the physical entity to cast the ray
     * @return true if the ray collides with an entity, false if not
     */
    public boolean cast(Physical ent, float angle) {
        //retrieves the owners position and rotation
        setX(ent.getX()); 
        setY(ent.getY());
        setRotation(ent.getRotation());
        
        //reset the ray properties
        setForceX(0);        
        setForceY(0);
        setCollidable(true);
        hit = null;
        target = ent;
        
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
        distance = target.getDistanceToEntity(this);
        //check if object hit is an Entity
        return hit instanceof Entity; //did not collide with entity
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
        if(col != target) {
            hit = col;
            setCollidable(false);
        }
    }
}