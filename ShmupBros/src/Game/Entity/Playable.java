package Game.Entity;

import Game.State.GameState;
import org.newdawn.slick.Animation;
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
    private float health;
    private Color color;
    private int kills, deaths;
    
    /**
     * Default constructor creates the vehicle for us in game
     * @param size takes a float of the objects size (Default size is 32)
     */
    public Playable(float size) {
        super(size);
        setType("Playable");
        health = 0;
        color = new Color(256,256,256);
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
        
        if (!isAlive() && getCollidable()){
            setCollidable(false);
            health = 0;
            GameState.addEntity(new Explosion(32, this));
            deaths++;
            
        }
    }
    
    /**
     * Used to allow vehicle to perform an attack
     */
    public void attack(Projectile bullet) {
        GameState.addEntity(bullet);
    }
    
    /**
     * used to keep track of the players number of kills
     */
    public void addKill() { kills++; }
        
    /**
     * Respawn the player 
     */
    public void respawn() { 
        health = 500;
        setCollidable(true);
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
     * @param gc The Current Game Container (screen)
     */
    @Override public void update() {
        applyFriction();
        modX(getForceX());
        modY(getForceY());
    }
    
    /**
     * Renders the current frame for  the vehicle
     * @param graphics The SLick2d/LWJGL graphics
     */
    @Override public void render(Graphics graphics) {
        if (!isAlive())
            return;
        
        sprite.setRotation(getRotation());
        sprite2.setRotation(getRotation());
        if(System.nanoTime()/100 % 2 == 0)
            sprite.draw(getX()-getSize(), getY()-getSize(), color);
        else
            sprite2.draw(getX()-getSize(), getY()-getSize(), color);
       
    }
}