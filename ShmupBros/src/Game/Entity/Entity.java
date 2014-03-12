package Game.Entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Entity: Base class for items in the game
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 */

public class Entity {
    private float x,y,rotation;
    private String type;
    private long ID;
    
    /**
     * Default constructor for Entity base class
     * @param id used to set the ID of the entity
     */
    public Entity(long id) {
        type = "Entity";
        ID = id;
    }

    /**
     * Used to retrieve the ID of the entity
     * @return the id of the entity
     */
    public long getID() { return ID; }
    
    /**
     * Used to set the ID of the current entity
     * @param ID the id to set the entity's id to
     */
    public void setID(long ID) { this.ID = ID; }
    
    /**
     * Used to retrieve the entity type
     * @return the type of entity
     */
    public String getType() { return type; }
    
    /**
     * Used to set the entity type
     * @param type a string contain the entity type
     */
    public void setType(String type) { this.type = type; }
    
    /**
     * Used to set the entity's x location
     * @param x the x position of the entity
     */
    public void setX(float x) { this.x = x; }
    
    /**
     * Used to modify the entity's x location
     * @param x the x displacement of the entity
     */
    public void modX(float x) { this.x += x; }
    
    /**
     * Used to retrieve the entity's x location
     * @return the x position of the entity
     */
    public float getX() { return x; }
    
    /**
     * used to set the y position of the entity
     * @param y the y position of the entity
     */
    public void setY(float y) { this.y = y; }
    
    /**
     * Used to modify the entity's y location
     * @param y the y displacement of the entity
     */
    public void modY(float y) { this.y += y; }
    
    /**
     * Used to get the y position of the entity
     * @return the y position as an int
     */
    public float getY() { return y; }
    
    /**
     * used to set the rotation of the entity
     * @param rotation the rotation of the entity in degrees
     */
    public void setRotation(float rotation) { this.rotation = rotation%360; }
    
    /**
     * Used to modify the entity's rotation
     * @param rotation the rotation in degrees to change
     */
    public void modRotation(float rotation) { setRotation(this.rotation + rotation); }
    
    /**
     * Used to get rotation of the entity
     * @return the rotation as an float
     */
    public float getRotation() { return rotation; }
    
    /**
     * base function for updating entities
     */
   public void update() { } 
    
    /**
     * base function for rendering entities
     * @param g graphics object
     */
    public void render(Graphics g) { }
    
    /**
     * Calculate rotation needed to face the target entity
     * @param ent The target entity
     * @return Rotation needed
     */
    public float getRotationToEntity(Entity ent){
        //return (float) Math.max(ent.rotation, this.rotation) - Math.min(ent.rotation, this.rotation);
        float x = ent.x - this.x;
        float y = ent.y - this.y;
        System.out.println((Math.atan2(y, x) * 180 / Math.PI) + " Degrees");
        return (float)(Math.atan2(y, x) * 180 / Math.PI);
    }
    
    /**
     * Calculate the distance to the target entity
     * @param ent The Target entity
     * @return Distance
     */
    public float getDistanceToEntity(Entity ent){
        return (float) Math.sqrt((ent.y - this.y) * (ent.y - this.y) + (ent.x - this.x) * (ent.x - this.x));
    }
}