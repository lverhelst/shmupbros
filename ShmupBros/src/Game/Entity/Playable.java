package Game.Entity;

import Ai.Ray;
import Communications.MCManager;
import Game.State.GameState;
import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
* Playable used to manage the status and location of the vehicle 
* @authors Daniel Finke, Emery Berg, Leon Verhelst
*/

public class Playable extends Physical{
    private static Image sprite, sprite2;    
    private float health, rotationSpeed, speed;
    private final int TOTAL_HEALTH = 100;
    private Color color;
    private int kills, deaths;
    private static boolean  showName;
    private long lastShot, deathTime;
    
    /**
     * Default constructor creates the vehicle for us in game
     * @param size takes a float of the objects size (Default size is 32)
     */
    public Playable(float size) {
        super(size);
        setType(Entity.TYPE.PLAYABLE);
        health = 0;
        color = new Color(256,256,256);
        rotationSpeed = 3;
        speed = 2;
        deathTime = System.currentTimeMillis();
    }
    
    /**
     * Used to initialize the playable object
     */
    public static void init() {
        try{
            sprite = new Image("Assets/Sprites/LocalPlayer1.png");
            sprite2 = new Image("Assets/Sprites/LocalPlayer2.png");
        } catch(SlickException e) {
            System.out.println("Failed to load player image: " + e.toString());
        }
    }
    
    /**
     * @return returns an Image of the vehicles base sprite
     */
    public Image getSprite() { return sprite; }
    
    /**
     * @return returns the players health
     */
    public float getHealth() { return health; }
        
    /**
     * @return returns the players color
     */
    public Color getColor() { return color; }
    
    /**
     * @return returns the number of kills the play currently has
     */
    public int getKills() { return kills; }
    
    /**
     * Gets the speed of the playable object
     * @return the speed of the object
     */
    public float getSpeed() { return speed; }
    
    /**
     * Gets the rotationspeed of the playable object
     * @return the rotationspeed of the object
     */
    public float getRotationSpeed() { return rotationSpeed; }
    
    /**
     * sets the players current color
     * @param color takes a color and sets the players color
     */
    public void setColor(Color color) { this.color = color; }
    
    /**
     * Method used to modify to the health of the current object
     * @param amount Amount of health to modify
     */
    @Override public void damage(float amount) {

        if (health > 0)
            health -= amount;
        
        //has been killed
        if (!isAlive() && getCollidable()){
            setCollidable(false);
            health = 0;
            GameState.addEntity(new Explosion(128, this));
            deaths++;
            deathTime = System.currentTimeMillis();
        }
    }  
    
    public int getDeaths(){
        return deaths;
    }
    
    public long getDeathTime(){
        return deathTime;
    }
    
    /**
     * Used to allow vehicle to perform an attack
     */
    public void attack() {
        if (lastShot + 101 < System.currentTimeMillis()) {
            GameState.addEntity(new Projectile(16,this));
            lastShot = System.currentTimeMillis();

            if (MCManager.getSender() != null)
                MCManager.getSender().sendAttack(getID()); //calls attack and sends the result
        }            
    }
    
    /**
     * used to keep track of the players number of kills
     */
    public void addKill() { kills++; }
        
    /**
     * Respawn the player 
     */
    public void respawn() { 

        health = TOTAL_HEALTH;
        setCollidable(true);
    }
    
    public int getTotalHealth(){
        return TOTAL_HEALTH;
    }
    
    /**
     * @return True if Alive, if Dead: False
     */
    public boolean isAlive(){
        if(health > 0)
            return true;
        return false;
    }
    
    /**
     * Updates the rotation of the sprite and applies any forces acting on it
     */
    @Override public void update() {
        if(isAlive()) {
            applyFriction();
            modX(getForceX());
            modY(getForceY());
        }
    }
    
    /**
     * @return the showName
     */
    public static boolean isShowName() {
        return showName;
    }

    /**
     * @param aShowName the showName to set
     */
    public static void setShowName(boolean aShowName) {
        showName = aShowName;
    }
   
       
    /**
     * Renders the current frame for  the vehicle
     * @param graphics The SLick2d/LWJGL graphics
     */
    @Override public void render(Graphics graphics) {
        if (!isAlive()) {
            return;
        }
        
        if(GameState.isShowName()){
            graphics.drawString(this.getIdentifier(), getX(), getY() - 2 * getSize());
            
        }
        sprite.setRotation(getRotation());
        sprite2.setRotation(getRotation());
        if(System.nanoTime()/100 % 2 == 0)
            sprite.draw(getX()-getSize(), getY()-getSize(), color);
        else
            sprite2.draw(getX()-getSize(), getY()-getSize(), color);      
    }
}