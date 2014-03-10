package Game.Map;

import org.newdawn.slick.Graphics;

/**
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 * This class sets up the tiles for the map to use.
 */
public class Tile {
    private float x;
    private float y;
    private boolean passable;
	
	// Tile constructor
    public Tile(float x, float y, boolean pass) { 
        passable = pass;
        this.x = x;
        this.y = y;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public boolean getPassable() { return passable; }
    
    public void setPassable(boolean pass) { passable = pass; }
    
    /**
     * Base render method
     * @param graphics The SLick2d/LWJGL graphics
     */
    public void render(Graphics graphics) { }
}