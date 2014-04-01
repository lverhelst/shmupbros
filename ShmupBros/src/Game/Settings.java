package Game;

import Ai.Rule;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.newdawn.slick.Color;

/**
 * Static class used to hold saved values and load stored settings and save them
 * @author Emery
 */
public class Settings {
    public static ArrayList<Rule> rules = new ArrayList();
    public static boolean multiplayer;
    public static boolean showPath;
    public static boolean showRay;
    public static boolean showSearchSpace;
    public static String playerName;
    public static Color color;
    public static int height;
    public static int width;
    public static int numBots;
    
    /**
     * Loads the config file into the application
     * @return true if successful
     */
    public static boolean loadConfig() {
         try{
            //open file (This text file is the Knowledge Base for the Expert System)
            BufferedReader br = new BufferedReader(new FileReader("config.ini"));
            String line = br.readLine();            
            String[] parts;            
            
            while(line != null) {
                parts = line.split(":");
                
                switch(parts[0]) {
                    case "Resolution":
                        parts = parts[1].split(",");
                        width = Integer.parseInt(parts[0]);
                        height = Integer.parseInt(parts[1]);
                        break;
                    case "Player Name":
                        playerName = parts[1];
                        break;
                    case "Color":
                        parts = parts[1].split(",");
                        float c = Float.parseFloat(parts[0]);
                        float c2 = Float.parseFloat(parts[1]);
                        float c3 = Float.parseFloat(parts[2]);
                        
                        color = new Color(c,c2,c3);
                        break;
                    case "Multiplayer":                        
                        multiplayer = Boolean.parseBoolean(parts[1]);
                        break;
                    case "Show Path":
                        showPath = Boolean.parseBoolean(parts[1]);
                        break;
                    case "Show Ray":
                        showRay = Boolean.parseBoolean(parts[1]);
                        break;    
                    case "Show Search Space":
                        showSearchSpace = Boolean.parseBoolean(parts[1]);
                        break;    
                    case "Number of Bots":
                        numBots = Integer.parseInt(parts[1]);
                        break;      
                    case "Rule":
                        parts = parts[1].split("!");
                        String set = parts[0];
                        String name = parts[1];
                        
                        String[] x_vals = parts[2].split(",");
                        String[] y_vals = parts[3].split(",");
                        
                        double[] x_coords = new double[x_vals.length];
                        double[] y_coords = new double[y_vals.length];
                        
                        for(int i = 0; i < x_vals.length; ++i) {
                            x_coords[i] = Double.parseDouble(x_vals[i]);
                            y_coords[i] = Double.parseDouble(y_vals[i]);
                        }
                        
                        rules.add(new Rule(set,name,x_coords,y_coords));
                        break;                        
                }
                
                line = br.readLine();
            }
            
            //close connection  
            br.close();
        }catch(IOException | NumberFormatException e){
            System.err.println(e);
            return false;
        }
         
         return true;
    }
    
    /**
     * Used to save the loaded settings
     * @return true if save was successful
     */
    public static boolean saveConfig() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("config.ini"));
            
            bw.write("Resolution:" + width + "," + height);
            bw.newLine();
            bw.write("Player Name:" + playerName);
            bw.newLine();
            bw.write("Color:" + color.r + "," + color.b + "," + color.g);
            bw.newLine();
            bw.write("Multiplayer:" + multiplayer);
            bw.newLine();
            bw.write("Show Path:" + showPath);
            bw.newLine();
            bw.write("Show Ray:" + showRay);
            bw.newLine();
            bw.write("Show Search Space:" + showSearchSpace);
            bw.newLine();
            bw.write("Number of Bots:" + numBots);
            bw.newLine();
            
            //write out all rules
            for(Rule r: rules) {                
                String x_coords = "";
                String y_coords = "";
                
                for(double x: r.getX_coord()) {
                    x_coords += x + ",";
                }
                
                for(double y: r.getY_coord()) {
                    y_coords += y + ",";
                }
                
                //remove last comma
                x_coords = x_coords.substring(0, x_coords.length() - 1);
                y_coords = y_coords.substring(0, y_coords.length() - 1);
                
                bw.write("Rule:" + r.getSet() + "!" + r.getName() + "!" + x_coords + "!" + y_coords);
                bw.newLine();
            }
            
            bw.flush();
            bw.close();
        }catch(IOException | NumberFormatException e){
            System.err.println(e);
            return false;
        }
        return true;
    }
}