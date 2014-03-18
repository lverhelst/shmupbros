package Game.Map;

import Game.State.GameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 * This class sets up the tiles for the map to use.
 */
public class Tile {
    private float x;
    private float y;
    private boolean passable;
    public boolean pathnode;
    public Tile parent;
    
	
	// Tile constructor
    public Tile(float x, float y, boolean pass) { 
        passable = pass;
        this.x = x;
        this.y = y;
        parent = null;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public boolean getPassable() { return passable; }
    
    public void setPassable(boolean pass) { passable = pass; }
    
    /**
     * Base render method
     * @param graphics The SLick2d/LWJGL graphics
     */
    public void render(Graphics graphics) { 
        if(pathnode && GameState.isShowPath()){
            graphics.setColor(Color.green);
            graphics.drawRect(x, y, 32, 32);
        }
    
    }
}