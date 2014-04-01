package Game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 * Main: Starts the application
 * @authors Daniel, Emery, Leon
 */
public class Main {
    public static void main(String[] args) throws SlickException {
        Settings.loadConfig();
        
	AppGameContainer app = new AppGameContainer(new StateManager());
        
        //TODO: Add config loading for defaults (player name/color, screen size, fullscreen, controls, FPS on/off)
        app.setDisplayMode(Settings.width, Settings.height, false);
        app.setShowFPS(true);
        app.setAlwaysRender(true); // Display all frames even when not in focus	
        
        app.start();
  }
}