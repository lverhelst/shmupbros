package Game.Map;

import Game.Entity.Entity;
import Game.State.GameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * @author Daniel Finke, Emery Berg, Leon Verhelst
 * This class sets up the tiles for the map to use.
 */
public class Tile extends Entity {
    public boolean passable;
    
    //A* elements, used while creating path
    public boolean pathnode;
    public boolean isClosed;
    public Tile parent;
    public double score; 
    
    /**
     * Default constructor for a tile
     * @param x the x location
     * @param y the y location
     * @param pass if the tile is passable or not
     */
    public Tile(float x, float y, boolean pass) {
        super(1);
        setX(x);
        setY(y);
        
        passable = pass;
        parent = null;
    }
    
    /**
     * Used to find out if the tile is passable
     * @return true if tile is passable
     */
    public boolean getPassable() { return passable; }
    
    /**
     * Used to set if a tile is passable
     * @param pass boolean true if passable
     */
    public void setPassable(boolean pass) { passable = pass; }
    
    /**
     * Base render method
     * @param graphics The SLick2d/LWJGL graphics
     */
    @Override public void render(Graphics graphics) { 
        if(isClosed && GameState.isShowSearchSpace()){
            graphics.setColor(Color.cyan);
            graphics.drawRect(getX(), getY(), 32, 32);
            graphics.drawString((int)score +"", getX(), getY());
        }
        
        if(pathnode && GameState.isShowPath()){
            graphics.setColor(Color.green);
            graphics.drawRect(getX(), getY(), 32, 32);
            
            graphics.drawString((int)score +"", getX(), getY());
        }
    }
}