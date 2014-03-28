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
	AppGameContainer app = new AppGameContainer(new StateManager());
        
        //TODO: Add config loading for defaults (player name/color, screen size, fullscreen, controls, FPS on/off)
        app.setDisplayMode(1024, 600, false);
        app.setShowFPS(true);
        app.setAlwaysRender(true); // Display all frames even when not in focus
	
        app.start();
        
        //testing rules
        double[] rx1 = {0,10,20};
        double[] ry1 = {0,0,1};
        double[] rx2 = {0,10,20};
        double[] ry2 = {1,1,0};
        
        Rule r1 = new Rule("rule1", rx1, ry1);
        Rule r2 = new Rule("rule2", rx2, ry2);
        
        System.out.println("Test1: eval 15 expect 0.5: " + r1.evaluate(15));
               
        System.out.println("Test2: eval 15 expect 0.5: " + r2.evaluate(15)); 
        
        Rule r3 = r1.aggregate(r2);
        for(int i = 0; i < r3.x_coord.length; i++){
            System.out.println(r3.x_coord[i] + "," + r3.y_coord[i]);
        }
        
    }
}