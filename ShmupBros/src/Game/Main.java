package Game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 * Main: Starts the application
 * @authors Daniel, Emery, Leon
 */
public class Main {
    public static void main(String[] args) throws SlickException {
	AppGameContainer app = new AppGameContainer(new StateManager());
        
        //TODO: Add config loading for defaults (player name/color, screen size, fullscreen, controls, FPS on/off)
        app.setDisplayMode(1024, 600, false);
        app.setShowFPS(true);
        app.setAlwaysRender(true); // Display all frames even when not in focus
	
        app.start();
    }
}