package Game.Entity;

/**
 * Physical: Used for entities which can collide 
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 */
public class Physical extends Entity {
    private float forceX;
    private float forceY;
    private float size;
    private boolean collidable;
    
    /**
     * Default constructor for creating collidable entities
     * @param size float for the size of the entity
     */
    public Physical(float size) { 
        super(System.nanoTime());
        setType("Physical");
        
        this.size = size/2;
        collidable = true;
    }
    
    /**
     * Used to apply a force to the object
     * @param force The force to be applied
     * @param angle The angle the force is being applied at
     */
    public void applyForce(float force, float angle) {
        modForceX(force * (float)Math.cos(Math.toRadians(angle)));
        modForceY(force * (float)Math.sin(Math.toRadians(angle)));
    }
   
    /**
     * Used to simulate frictions affect on the object
     */
    public void applyFriction() {
        forceX *= 0.85;
        forceY *= 0.85;
    }
    
    /**
     * @param amount the amount of damage to apply
     */
    public void damage(float amount) { }

    /**
     * Base function used when objects collide
     * @param col Physical object which has been collided with
     */
    public void Collide(Physical col) {
        float x = col.getForceX();
        float y = col.getForceY();
        col.setForceX(getForceX());
        col.setForceY(getForceY());
        setForceX(x);
        setForceY(y);
    }
    
    /**
     * Base function used when an object collides with the world
     */
    public void Collide() {
        setForceX(-getForceX());
        setForceY(-getForceY());
    }
    
    /**
     * Checks to see if the two objects are currently colliding
     * @param col takes a physical object to check against
     * @return returns true if objects are colliding
     */
    public boolean isColliding(Physical col) {
        if(intersectsWidth(col) && intersectsDepth(col))
            return true;
        return false;
    }
        
    /**
     * Used by the isColliding function to check if the 2 objects collide width
     * @param col takes a physical object to check against
     * @return returns true if objects are colliding
     */
    public boolean intersectsWidth(Physical col) {
        if(getX() - getSize() < col.getX() + col.getSize() &&
                getX() + getSize() > col.getX() - col.getSize())
            return true;
        return false;
    }
    
    /**
     * Used by the isColliding function to check if the 2 objects collide depth
     * @param col takes a collidable object to check against
     * @return returns true if objects are colliding
     */
    public boolean intersectsDepth(Physical col) {
        if(getY() - getSize() < col.getY() + col.getSize() &&
                getY() + getSize() > col.getY() - col.getSize())
            return true;
        return false;
    }   
    
    /**
     * Used to set if the object can be collided with
     * @param collidable a boolean value true if collidable
     */
    public void setCollidable(boolean collide) { this.collidable = collide;}
    
    /**
     * used to set the size of the entity
     * @param size float containing the size of the entity
     */
    public void setSize(float size) { this.size = size; } 
    
    /**
     * Used to set the force acting on the X axis
     * @param xf the X axis force
     */
    public void setForceX(float xf) { forceX = xf; }
    
    /**
     * Used to set the force acting on the Y axis
     * @param yf the Y axis force
     */
    public void setForceY(float yf) { forceY = yf; }
    
    /**
     * Used to modify the position of an object using distance and angle
     * @param distance The distance to be move
     * @param angle The angle the distance is being traversed
     */
    public void modPosition(float distance, float angle) {
        modX(distance * (float)Math.cos(Math.toRadians(angle)));
        modY(distance * (float)Math.sin(Math.toRadians(angle)));
    }
    
    /**
     * used to modify the size of the entity
     * @param size float amount of the change in size (radius)
     */
    public void modSize(float size) { this.size += size; }   
    
    /**
     * Used to modify force on the X axis
     * @param xf force is modified by the passed amount
     */
    public void modForceX(float xf) { forceX += xf; }
    
    /**
     * Used to modify force on the Y axis
     * @param yf force is modified by the passed amount
     */
    public void modForceY(float yf) { forceY += yf; }
    
    /**
     * Used to return if the object is collidable
     * @return a boolean value of the objects collidability
     */
    public boolean getCollidable() {return collidable; }
    
    /**
     * Used to return the size of the entity stored as radius
     * @return float contain the size of the entity
     */
    public float getSize() { return size; }
    
    /**
     * @return returns the current force applied on the x axis 
     */
    public float getForceX() { return forceX; }
    
    /**
     * @return returns the current force applied on the x axis 
     */
    public float getForceY() { return forceY; }
    
    /**
     * Used to retrieve the force acting on the object
     */
    public float getForce() {
        return (float)Math.sqrt((forceX*forceX) + (forceY*forceY));
    }
}