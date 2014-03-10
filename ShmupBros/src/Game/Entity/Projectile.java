package Game.Entity;

import Game.State.GameState;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Projectile: used to draw and handle each bullet
 * @author Daniel Finke, Emery Berg and Leon Verhelst
 */

public class Projectile extends Physical{
    private static Image sprite, sprite2, sprite3, sprite4;
    private Animation anim;
    private Playable owner;
    private float damage;
    
    public Projectile(float size, Playable owner) {
        super(size);
        setType("Projectile");
        
        this.owner = owner;
        
        damage = 35;

        setX(owner.getX()); //retrieves the owners position and rotation
        setY(owner.getY());
        setRotation(owner.getRotation());
        modPosition(48, getRotation()); //moves away from owner
        
        applyForce(16, getRotation());
        
        anim = new Animation();
        anim.addFrame(sprite, 100);
        anim.addFrame(sprite2, 100);
        anim.addFrame(sprite3, 100);
        anim.addFrame(sprite4, 100);
    }
    
    public static void init() {
        try {
            sprite = new Image("Assets/Sprites/Bullet1.png");
            sprite2 = new Image("Assets/Sprites/Bullet2.png");
            sprite3 = new Image("Assets/Sprites/Bullet3.png");
            sprite4 = new Image("Assets/Sprites/Bullet4.png");            
        } catch (SlickException slickEX) {
            System.out.println("Failed to load remote player image: " + slickEX.toString());
        }
    }
    
    @Override public void Collide() { 
        setForceX(-getForceX());
        setForceY(-getForceY());
        setCollidable(false);
    }
    
    /**
     * Base function used when objects collide
     * @param col Physical object which has been collided with
     */
    @Override public void Collide(Physical col) {
        col.damage(damage);
        
        if(col.getType().equals("Playable") && !col.getCollidable())
            owner.addKill();            
        
        setCollidable(false);
    }
    
    /**
     * @return returns the bullets owner 
     */
    public Playable getOwner() { 
        return owner;
    }
    
    @Override public void update() {
        modX(getForceX());
        modY(getForceY());
        
        if(!getCollidable())
            GameState.removeEntity(this);
    }
    
    /**
     * Renders the current frame for  the vehicle
     * @param graphics The SLick2d/LWJGL graphics
     */
    @Override public void render(Graphics graphics) {
        anim.draw(getX()-getSize()/2, getY()-getSize()/2);
    }
}