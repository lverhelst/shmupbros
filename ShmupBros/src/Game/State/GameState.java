package Game.State;

import Communications.MCManager;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.*;

import Game.AIManager;
import Game.Entity.Bot;
import Game.Entity.Explosion;

import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.Entity.Projectile;
import Game.Map.Concrete;
import Game.Player;
import Game.Map.Map;
import Game.Entity.Entity.TYPE;
import Game.Map.Tile;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Font;
import org.newdawn.slick.TrueTypeFont;

import org.newdawn.slick.gui.TextField;

/**
 * GameState: Used to start and perform the game logic
 * @authors Daniel, Emery, Leon
 */
public class GameState extends BasicGameState {    
    public final int ID; //holds the current states ID
    private static ArrayList<Physical> entities = new ArrayList();
    private static Random rand = new Random();
    private static Map map;
    private static String logString = "Welcome to SHMUPBROS";
    private float offsetX, offsetY;
    private Player player;
    private MCManager server; 
    private AIManager ai;
    private long curtime, lastpathfind;
    private TextField log;
    private Font m_font;
    private static boolean showPath, showDirections, showSearchSpace, showName;
    
    
    TrueTypeFont font;  
    
    private boolean findpath = true;
     ArrayList<Tile> path = new ArrayList<Tile>();        
    
    /**
    * Constructor which takes an integer parameter for state ID
    * @param id Tells which state the game is in
    */
    public GameState(int id){ 
        ID = id;
        player = new Player("PLAYER");
        ai = new AIManager();
        
        int num_bots = 1;

        for(int i = 0; i < num_bots; i++){
            Bot p = new Bot(32f);
            p.setIdentifier("BOT" + i);     
            ai.addAI(p);
            p.setTarget(player.getTarget());
        } 
        
        showPath = false;
        showDirections = false;
        showSearchSpace = false;
        showName = false;
    }
    
    /**
     * Used to connect the play to any active games running
     * @param group the group to connect to
     * @param port the port to connect on
     */
    public void connect(String group, int port) {
        server = new MCManager();
        server.connect(group, port, player.getTarget().getID());
        player.connect();
    }
    
    /**
     * Initialize the require components for the game
     * @param gc The game container
     * @param sbg The StatebaseGame controller
     */
    @Override public void init(GameContainer gc, StateBasedGame sbg) {
        offsetX = gc.getWidth()/2;
        offsetY = gc.getHeight()/2;
        
        map = new Map("Assets/level.map", gc.getScreenWidth()/32, (gc.getScreenHeight())/32);
        curtime = System.currentTimeMillis();
        
        m_font = new Font("Verdana", Font.BOLD, 32);
        TrueTypeFont ttf = new TrueTypeFont(m_font, true);
        log = new TextField(gc, ttf , 0, 0, 100, 100);

        Projectile.init();
        Playable.init();
        Concrete.init();
        Explosion.init();
        
        spawn(player.getTarget());
        
        font = new TrueTypeFont(new java.awt.Font(java.awt.Font.SERIF,java.awt.Font.BOLD , 10), false);
    }
    
    /**
      * @return the current world map
      */
     public static Map getMap() { return map; }
   
    /**
     * @return Current State ID
     */
    @Override public int getID() { return ID; }
    
    /** 
     * @return returns true once the request has been sent 
     */
    public boolean closeRequested(){
        player.disconnect();
        return true;
    }
    
    /**
     * @return returns an ArrayList of the active entities in game
     */
    public static synchronized ArrayList<Physical> getEntities() {
        return entities;
    }
    
    /**
     * Used to add an entity to the game
     * @param entity takes an Entity and adds it to the game
     */
    public static synchronized void addEntity(Physical entity) {
        entities.add(entity);
    }
    
    /**
     * Used to remove an entity from the game
     * @param entity takes the entity which is to be removed
     */
    public static synchronized void removeEntity(Physical entity) {
        entities.remove(entity);
    }
    
    /**
     * Used to set the active list of entities in the game
     * @param newentities ArrayList of entities to the replace the current list
     */
    public static synchronized void setEntities(ArrayList<Physical> newentities) {
        entities = newentities;
    }
    
    /**
     * Adds a text message to the log area
     * @param text 
     */
    public static synchronized void addText(String text){
        logString  = text + "\r\n" + logString;
    }
    
    /**
     * Searches the play area for a safe spawn zone
     * @param col the entity to spawn in the game world
     */
    public static void spawn(Playable col) {
        boolean check = false; 

        //look until a position is found
        while(!check) {
            check = true;
            
            col.setX(rand.nextFloat()*(map.getWidth()*32));
            col.setY(rand.nextFloat()*(map.getHeight()*32));
            
            for(int i = entities.size()-1; i >= 0; --i) 
                if(col != entities.get(i) && col.isColliding(entities.get(i))) 
                    check = false;
        
            int x = (int)((col.getX() + col.getForceX() - col.getSize())/32);
            int y = (int)((col.getY() + col.getForceY() - col.getSize())/32);
        
            if(!map.getPassable(x, y) || !map.getPassable(x+1, y) || 
                !map.getPassable(x, y+1) || !map.getPassable(x+1, y+1)) 
                check = false;
        }
        //ensure the entity list has the entity
        if(!entities.contains(col))
            GameState.addEntity(col);
        
        col.respawn();
    }
    
    /**
     * Check if the entity is colliding with the world
     * @param col the entity to check if colliding
     */
    public void checkMapCollisions(Physical col) {
        int x = (int)((col.getX() + col.getForceX() - col.getSize())/32);
        int y = (int)((col.getY() + col.getForceY() - col.getSize())/32);
        
        if(col.getCollidable() && !map.getPassable(x, y) || !map.getPassable(x+1, y) || 
                !map.getPassable(x, y+1) || !map.getPassable(x+1, y+1)) 
            col.Collide();
    }
    
    /**
     * Check if the entity is colliding with another entity
     * @param col the entity to check
     */
    public void checkCollisions(Physical col) {
        //ensure the object can collide
        if(!col.getCollidable())
           return;
        
        for(int i = entities.size()-1; i >= 0; --i) {
            if(entities.get(i).getCollidable() && col != entities.get(i) && col.isColliding(entities.get(i))) {
                col.Collide(entities.get(i));
                
                if(entities.get(i).getType() == TYPE.PROJECTILE 
                        && entities.get(i).getCollidable())
                    entities.get(i).Collide(col);
            }                
        }
        
        checkMapCollisions(col);
    } 
    
    /**
     * Used to check if a point is within the map bounds
     * @param xp the x position to check
     * @param yp the y position to check
     * @return true if in bounds
     */
    public static boolean checkMapBounds(float xp, float yp) {        
        int x = (int)(xp/32);
        int y = (int)(yp/32);  
        
        return x > 0 && x < map.getWidth() && y < map.getHeight() && y > 0;
    }
    
    /**
     * Used to check if a single point is colliding 
     * @param xp the x position to check
     * @param yp the y position to check
     * @param ID optional ignores the given entity with the ID
     * @return the object which was collided with
     */
    public static Object checkCollision(float xp, float yp, long ID) {
        int x = (int)(xp/32);
        int y = (int)(yp/32);        
        
        if(!map.getPassable(x, y)) {
            return map.getTile(x, y);
        }
        
        for(Physical ent: entities) {
            if(ent.getID() != ID && ent.getCollidable() && x == (int)(ent.getX()/32) && y == (int)(ent.getY()/32)) {
                return ent;
            }
        }
        
        return null;
    }
    
    /**
     * Used to check if a single point is colliding with entities
     * @param xp the x position to check
     * @param yp the y position to check
     * @param ID optional ignores the given entity with the ID
     * @return the object which was collided with
     */
    public static Physical checkEntityCollision(float xp, float yp, long ID) {
        int x = (int)(xp/32);
        int y = (int)(yp/32);        
        
        for(Physical ent: entities) {
            if(ent.getID() != ID && x == (int)(ent.getX()/32) && y == (int)(ent.getY()/32)) {
                return ent;
            }
        }
        
        return null;
    }
    
    /**
     * Used to check if a single point is colliding with a wall
     * @param xp the x position to check
     * @param yp the y position to check
     * @return the object which was collided with
     */
    public static Tile checkWallCollision(float xp, float yp) {
        int x = (int)(xp/32);
        int y = (int)(yp/32);
        
        if(!map.getPassable(x, y)) {
            return map.getTile(x, y);
        }
        
        return null;
    } 
    
    /**
     * @return the showPath
     */
    public static boolean isShowPath() {
        return showPath;
    }
    
    public static void setShowPath(Boolean bool){
        showPath = bool;
    }

    /**
     * @return the showDirections
     */
    public static boolean isShowDirections() {
        return showDirections;
    }
    
    /**
     * @return the showDirections
     */
    public static boolean isShowSearchSpace() {
        return showSearchSpace;
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
      * Update the current player every 15 milliseconds
      * @param gc GameState container
      * @param sbg GameState state
      * @param i Unused
      */
    @Override public void update(GameContainer gc, StateBasedGame sbg, int i) {
        if (System.currentTimeMillis() > curtime + 15) {
            player.update(gc.getInput()); //limites the speed at which update occur
            curtime = System.currentTimeMillis();
        
            //check the gameworld for collisions
            for(int j = entities.size()-1; j >= 0; j--) {
                checkCollisions(entities.get(j));                
                entities.get(j).update();
            }  
            
            //update the ai players
            ai.update();

        }
    }
   
    /**
     * renders the world and entities
     * @param gc Current GameState Container
     * @param sbg Current GameState state
     * @param graphics Graphics, as defined by Slick and LWJGL
     */
    @Override public void render(GameContainer gc, StateBasedGame sbg, Graphics graphics) {
        float x = offsetX - player.getTarget().getX();
        float y = offsetY - player.getTarget().getY();
        
        graphics.translate(x, y);
        
        for(int i = 0; i < entities.size(); i++) 
            entities.get(i).render(graphics);
        
        int activeX = -(int)(x/32);
        int activeY = -(int)(y/32);
        
        map.render(graphics, activeX, activeY); //draws the map

        graphics.resetTransform();

        player.render(graphics);     
        
        graphics.drawRect(gc.getWidth() - 352, gc.getHeight() - 90, 350, 100);
        graphics.setColor(Color.white);
        graphics.setFont(font);
        drawString(graphics, logString, gc.getWidth() - 350, gc.getHeight() - 100);
    }
    
    /**
     * Used to render text at a location
     * @param g the graphics object
     * @param text the string to display
     * @param x the x location
     * @param y the y location
     */
    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += font.getHeight(line));
    }
}