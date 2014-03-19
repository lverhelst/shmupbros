package Game;

import Communications.MCManager;
import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.State.GameState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import Game.Entity.Entity.TYPE;
import Game.State.Option;

/**
 * Player: Used to view and manage the local player and game
 * @authors Daniel, Emery, Leon
 */
public class Player {
    private Playable target;
    private String name;
    
    /**
     * Default contructor which sets the defaults and the players name
     * @param name the Players name
     */
    public Player(String name) {
        target = new Playable(32);
        target.setIdentifier(name + "1");
        this.name = name;
        
        if(Option.vehicle != null) {
            target.setColor(Option.vehicle.getColor());
        }
        
        //temporary default location
        target.setX(512);
        target.setY(512);
    }
    
    /**
     * Used to connect the player to the current online game
     */
    public void connect() {        
        MCManager.getSender().sendPosition(target);
    }
    
    /**
     * used to disconnect from the server
     */
    public void disconnect() {
        MCManager.getSender().sendReleasePlayer(target.getID());
    }
    
    /**
     * @return retrieves the players name
     */    
    public String getName() { return name; }
    
    /**
     * @return retrieves the players current target for control
     */
    public Playable getTarget() { return target; }
   
    /**
     * Default update method for controlling the players target and checking for
     * collisions
     * @param controls 
     */
    public void update(Input controls){   
        
        if(controls.isKeyDown(Input.KEY_TAB))
            Controller.update(target, Controller.MOVE.SHOWSCORE);
        else
            Controller.update(target, Controller.MOVE.HIDESCORE);
        
        if(controls.isKeyPressed(Input.KEY_C))            
            Controller.update(target, Controller.MOVE.SHOWNAMES);    
        
        if(controls.isKeyPressed(Input.KEY_V))            
            GameState.setShowPath(!GameState.isShowPath());
            
            //ensure movement only happens when alive
        //ensure respawning only happens when dead
        if(target.isAlive()){
            if (controls.isKeyDown(Input.KEY_W)) 
                Controller.update(target, Controller.MOVE.UP);

            if (controls.isKeyDown(Input.KEY_S)) 
                Controller.update(target, Controller.MOVE.DOWN);        

            if (controls.isKeyDown(Input.KEY_A)) 
                Controller.update(target, Controller.MOVE.ROTLEFT);        

            if (controls.isKeyDown(Input.KEY_D)) 
                Controller.update(target, Controller.MOVE.ROTRIGHT);        

            if (controls.isKeyDown(Input.KEY_RCONTROL)) 
                Controller.update(target, Controller.MOVE.FIRE); 
        }else{
            if(controls.isKeyPressed(Input.KEY_ENTER))
                Controller.update(target, Controller.MOVE.RESPAWN);   
        }
    }
    
    /**
     * Renders the hud for the current player
     * @param graphics The SLick2d/LWJGL graphics
     */
    public void render(Graphics graphics) {
        graphics.setColor(Color.white);
        graphics.drawString("Kills: " + target.getKills(), 900, 20);
        
        //render health bar
        graphics.drawRect(19, 560, 201, 20);
        graphics.setColor(Color.red);
        graphics.fillRect(20, 561, target.getHealth() / target.getTotalHealth() * 200 , 19);
        
        //shows the scoreboard if option is selected
         if(Controller.showScore){
            graphics.setColor(Color.white);
            String score = "Score:\r\n";
            int i = 1;
            ArrayList<Playable> p = new ArrayList<>();
            
            for(Physical e : GameState.getEntities()){
                if(e.getType() == TYPE.PLAYABLE){
                    p.add((Playable)e);                        
                }
            }
            
            //orders the players based on scores
            Collections.sort(p, new Comparator<Playable>(){
               public int compare(Playable a, Playable b){
                   return b.getKills() - a.getKills() ;
               } 
            });
            
            for(Playable pl: p){
                score += i + ": " + pl.getIdentifier() + " " + ((Playable)pl).getKills() + "\r\n";
                   i++;
            }
            drawString(graphics, score, 51, 51);
        }
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
            g.drawString(line, x, y += 20);
    }
}