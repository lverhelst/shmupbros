package Game;

import Ai.Rule;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * @author Emery
 */
public class RuleDisplay extends javax.swing.JPanel {
    private double[] x_coords;
    private double[] y_coords;
    private double x_max;
    private double y_max;
    private int x_scale;
    private int y_scale;
    
    /**
     * Creates new form RuleDisplay
     * @param rule the rule to display
     */
    public RuleDisplay(Rule rule) {  
        setRule(rule);
    }
    
    /**
     * Used to change the displayed rule
     * @param rule the rule to display
     */
    public void changeRule(Rule rule) {  
        setRule(rule);
    }
    
    /**
     * private method which does the work of setting a rule
     * @param rule the rule to set
     */
    private void setRule(Rule rule) {        
        x_coords = rule.getX_coord();
        y_coords = rule.getY_coord();
        
        x_max = getMax(x_coords);
        y_max = getMax(y_coords);
        
        x_scale = (int)(getX()/x_max);
        y_scale = (int)(getY()/y_max); 
    }
    
    /**
     * Private method for finding the max value, used to scale graph
     * @param ar the double array to find max of
     * @return the max value found
     */
    private double getMax(double[] ar) {
        double max = 0;
        
        for(double val: ar) {
            if(val > max)
                max = val;
        }
        
        return max;
    }
    
    @Override public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(java.awt.Color.red);
        g2.setBackground(Color.yellow);
        
        int last_x = 0;
        int last_y = 0;
        
        for(int i = 0; i < x_coords.length; ++i) {
            int x = (int)x_coords[i];
            int y = (int)y_coords[i];
            
            g2.drawLine(x*x_scale, y*y_scale, last_x*x_scale, last_y*y_scale);
        }
    }
}