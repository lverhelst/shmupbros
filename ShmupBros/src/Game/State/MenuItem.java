package Game.State;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

/**
* MenuItem used to manage the menu items in game states
* @author Emery Berg
*/

public class MenuItem {
    private Rectangle rect;
    private Image sprite;
    private Color color;
    
    /**
     * Default constructor for the menu item
     * @param file the file path to the image to be used
     * @param x the x position of the menu item
     * @param y the y position of the menu item
     */
    public MenuItem(String file, float x, float y) {
        try {
            sprite = new Image(file);
        } catch (SlickException e) {
            System.out.println("Error loading menu image: " + e.toString());
        }
        
        color = Color.lightGray;
        rect = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }
    
    /**
     * Used to check if a position is within the current menu item
     * @param x the x position to check
     * @param y the y position to check
     * @return returns a boolean value true if contained
     */
    public boolean contains(float x, float y) {
        if(rect.contains(x, y)) 
            color = Color.white;
        else
            color = Color.lightGray;
        
        return rect.contains(x, y);
    }
    
    /**
     * Used to retrieve the sprite used for this menu item
     * @return returns the sprite used for this menu item
     */
    public Image getSprite() { return sprite; }
    
    /**
     * renders the menu item
     * @param graphics Slick/LJWGL Graphics
     */
    public void render(Graphics graphics) {        
        sprite.draw(rect.getX(), rect.getY(), color);
    }
}