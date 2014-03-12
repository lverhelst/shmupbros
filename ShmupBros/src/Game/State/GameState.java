package Game.State;

import Communications.MCManager;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import Game.AIManager;
import Game.Bot;
import Game.Entity.Explosion;

import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.Entity.Projectile;
import Game.Map.Concrete;
import Game.Player;
import Game.Map.Map;

import java.util.ArrayList;
import java.util.Random;

/**
 * GameState: Used to start and perform the game logic
 * @authors Daniel, Emery, Leon
 */

public class GameState extends BasicGameState {
    public final int ID; //holds the current states ID
    private static ArrayList<Physical> entities = new ArrayList();
    private static Random rand = new Random();
    private static Map map;
    private float offsetX, offsetY;
    private Player player;
    private MCManager server; 
    private AIManager ai;
    private long curtime;
    
    /**
    * Constructor which takes an integer parameter for state ID
    * @param id Tells which state the game is in
    */
    public GameState(int id){ 
        ID = id;
        player = new Player("test");
        ai = new AIManager();
        int num_bots = 1;
        for(int i = 0; i < num_bots; i++){
            Bot p = new Bot((float)32.0);
            p.setIdentifier("BOT" + i);
            p.setTarget(player.getTarget());
            ai.addAI(p);
        }
        
        
    
    }
    
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

        Projectile.init();
        Playable.init();
        Concrete.init();
        Explosion.init();
        
        spawn(player.getTarget());
        addEntity(player.getTarget());
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
    
    public static void spawn(Playable col) {
        boolean check = false; 
        
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
        
        col.respawn();
    }
    
    public void checkMapCollisions(Physical col) {
        int x = (int)((col.getX() + col.getForceX() - col.getSize())/32);
        int y = (int)((col.getY() + col.getForceY() - col.getSize())/32);
        
        if(!map.getPassable(x, y) || !map.getPassable(x+1, y) || 
                !map.getPassable(x, y+1) || !map.getPassable(x+1, y+1)) 
            col.Collide();
    }
    
    public void checkCollisions(Physical col) {
        if(!col.getCollidable())
           return;
        
        for(int i = entities.size()-1; i >= 0; --i)
            if(col != entities.get(i) && col.isColliding(entities.get(i))) {
                col.Collide(entities.get(i));
                
                if(entities.get(i).getType().equals("Projectile") 
                        && entities.get(i).getCollidable())
                    entities.get(i).Collide(col);
            }
                
        
        checkMapCollisions(col);
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
        
        
            for(int j = entities.size()-1; j >= 0; j--) {
                checkCollisions(entities.get(j));
                entities.get(j).update();
            }
        
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
    }
}