package Ai;

import Game.Entity.Entity;
import Game.State.GameState;

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
     * @param x the x-axis origin coordinate as a float
     * @param y the y-axis origin coordinate as a float
     * @param angle the angle to move in
     * @param accuracy the level of detail for the ray, lower number is higher detail
     * @param ID optional, include if ray is not to collide with entity
     * @return true if the ray collides with an entity, false if not
     */
    public boolean cast(float x, float y, float angle, float accuracy, long ID) {
        float x2 = x;
        float y2 = y;
        hit = null; //reset the raycast
        
        //search until a collision happens
        while(hit == null && GameState.checkMapBounds(x2, y2)) {
            hit = (T)GameState.checkCollision(x2, y2, ID);
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