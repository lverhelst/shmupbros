package Ai;

import Game.Entity.Entity;
import Game.Entity.Physical;
import Game.State.GameState;
import static org.newdawn.slick.svg.NonGeometricData.ID;

/**
 * Used to cast a ray and find out what it hit and its distance
 * @author Emery
 */
public class Ray <T>{
    private float distance;
    private T hit;
    
    /**
     * Default constructor
     */
    public Ray() {}
    
    /**
     * Used to cast a ray from a position at an angle
     * @param angle the angle to move in
     * @param accuracy the level of detail for the ray, lower number is higher detail
     * @param the physical entity to cast the ray
     * @return true if the ray collides with an entity, false if not
     */
    public boolean cast(Physical ent, float angle, float accuracy) {
        float x2 = ent.getX() - ent.getSize()/2;
        float y2 = ent.getY() - ent.getSize()/2;
        float x = x2;
        float y = y2;
        hit = null; //reset the raycast
        
        //search until a collision happens
        while(hit == null && GameState.checkMapBounds(x2, y2)) {
            hit = (T)GameState.checkCollision(x2, y2, ent.getID());
            x2 += accuracy * (float)Math.cos(Math.toRadians(angle));
            y2 += accuracy * (float)Math.sin(Math.toRadians(angle));
        } 
        
        //calculate the distance traveled
        x = x - x2;
        y = y - y2;        
        distance = (float)(Math.sqrt(y * y) + (x * x));
        //check if object hit is an Entity
        return hit instanceof Entity; //did not collide with entity
    }
    
    /**
     * @return the object which the ray collided with 
     */
    public T getHit() {
        return hit;
    }
    
    /**
     * @return the distance to the object as a float 
     */
    public float getDistance() {
        return distance;
    }    
}