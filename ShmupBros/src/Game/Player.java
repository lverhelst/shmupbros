package Game;

import Communications.MCManager;
import Game.Entity.Physical;
import Game.Entity.Playable;
import Game.Entity.Projectile;
import Game.State.GameState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * Player: Used to view and manage the local player and game
 * @authors Daniel, Emery, Leon
 */
public class Player {
    private Playable target;
    private String name;
    private long lastShot;
    private boolean showScore;
    
    /**
     * Default contructor which sets the defaults and the players name
     * @param name the Players name
     */
    public Player(String name) {
        target = new Playable(32);
        target.setIdentifier(name + "1");
        this.name = name;
        lastShot = System.currentTimeMillis();
        
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
        boolean updatedLocation = false;  
         
        if(controls.isKeyDown(Input.KEY_TAB)){
            showScore = true;
        }
        else{
            showScore = false;
        }
        
        if(controls.isKeyPressed(Input.KEY_C))
            target.setShowName(!target.isShowName());
    
        
        if(!target.isAlive()){
            if(controls.isKeyPressed(Input.KEY_ENTER)){
                target.respawn();
                GameState.spawn(target);
            }
            return;
        }
        
        if (controls.isKeyDown(Input.KEY_W)) {
            target.applyForce(2, target.getRotation());
            updatedLocation = true;
        }
        if (controls.isKeyDown(Input.KEY_S)) {
            target.applyForce(-2, target.getRotation());
            updatedLocation = true;
        }
        
        if (controls.isKeyDown(Input.KEY_A)) {
            target.modRotation(-3);
            updatedLocation = true;
        }
        
        if (controls.isKeyDown(Input.KEY_D)) {
            target.modRotation(3);
            updatedLocation = true;
        }
        
        
        
        
        if (controls.isKeyDown(Input.KEY_RCONTROL)) {
            if (lastShot + 101 < System.currentTimeMillis()) {
                target.attack(new Projectile(16,target));
                lastShot = System.currentTimeMillis();
                if (MCManager.getSender() != null)
                    MCManager.getSender().sendAttack(target.getID()); //calls attack and sends the result
            }
        }        
   
        
        //only sends updates if player has moved
        if (updatedLocation & MCManager.getSender() != null) {
            MCManager.getSender().sendPosition(target);
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
        
         if(showScore){
            //render health bar
                 graphics.drawRect(49,69, 201, 201);
                 graphics.setColor(Color.white);
                 String score = "Score:\r\n";
                 int i = 1;
                 ArrayList<Playable> p = new ArrayList<>();
                 for(Physical e : GameState.getEntities()){
                     if(e.getType().equals("Playable")){
                         p.add((Playable)e);
                        
                     }
                 }
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
                 //graphics.fillRect(50,50, 200 , 200);
        }
    }
    
    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += 20);
    }
}