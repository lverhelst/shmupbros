package Game.Map;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 * This class sets up the tiles for the map to use.
 */
public class Concrete extends Tile {
    private static Image tile;
	
	// Tile constructor
    public Concrete(float x, float y, boolean pass) { 
        super(x, y, pass);
    }
    
    public static void init() {       
        try{
            tile = new Image("Assets/Concrete.png");
        } catch(SlickException e) {
            System.out.println("Failed to load concrete image: " + e.toString());
        }
    }
    
    /**
     * Renders the current concrete block
     * @param graphics The SLick2d/LWJGL graphics
     */
    @Override public void render(Graphics graphics) {
        tile.draw(getX(), getY()-16);
    }
}