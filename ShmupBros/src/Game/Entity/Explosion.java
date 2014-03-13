package Game.Entity;

import Game.State.GameState;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
* Explosion
* @author Emery Berg
*/

public class Explosion extends Physical {
    private static Image sprite, sprite2, sprite3, sprite4, sprite5, sprite6, sprite7;
    private Animation anim;
    
    public Explosion(float size, Entity owner) {
        super(size);
        setX(owner.getX());
        setY(owner.getY());
        
        anim = new Animation();
        anim.addFrame(sprite, 100);
        anim.addFrame(sprite2, 100);
        anim.addFrame(sprite3, 100);
        anim.addFrame(sprite4, 100);
        anim.addFrame(sprite5, 100);
        anim.addFrame(sprite6, 100);
        anim.addFrame(sprite7, 100);
        anim.setLooping(false);
        this.setCollidable(false);
    }
    
    public static void init() {
        try {
            sprite = new Image("Assets/Sprites/Expl1.png");
            sprite2 = new Image("Assets/Sprites/Expl2.png");
            sprite3 = new Image("Assets/Sprites/Expl3.png");
            sprite4 = new Image("Assets/Sprites/Expl4.png");
            sprite5 = new Image("Assets/Sprites/Expl5.png");
            sprite6 = new Image("Assets/Sprites/Expl6.png");
            sprite7 = new Image("Assets/Sprites/Expl7.png");
        } catch (SlickException slickEX) {
            System.out.println("Failed to load remote player image: " + slickEX.toString());
        }
    }
    
    @Override public void Collide() { }
    @Override public void Collide(Physical col) { }
    
    @Override public void update() {
        if(anim.isStopped())
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