package Game;

import Ai.Rule;
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
        
        //testing rules
        /*
        double[] rx1 = {0,10,20,};
        double[] ry1 = {1,0,1};
        double[] rx2 = {0,10,20};
        double[] ry2 = {0,1,0};
        
        Rule r1 = new Rule("test","close", rx1, ry1);
        Rule r2 = new Rule("test","middle", rx2, ry2);
        double value = 96;
        
        System.out.println(r1.getName() + ": eval " + value + " expect 1.0: " + r1.evaluate(value));               
        System.out.println(r2.getName() + ": eval " + value + " expect 0.5: " + r2.evaluate(value)); 
        
        Rule r3 = r1.aggregate(r2);
        System.out.println(r3);
                */
  }
}