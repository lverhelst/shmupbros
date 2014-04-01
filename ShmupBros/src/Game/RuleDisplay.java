package Game;

import Ai.Rule;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * @author Emery
 */
public class RuleDisplay extends javax.swing.JPanel {
    private double[] x_coords;
    private double[] y_coords;
    private double x_total;
    private double y_max;
    private double x_scale;
    private double y_scale;
    
    /**
     * Creates new form RuleDisplay
     */
    public RuleDisplay() {
        super();
        
        setRule(Settings.rules.get(0));
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
        
        x_total = getTotal(x_coords);
        y_max = getMax(y_coords); 
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
    
    /**
     * Private method for finding the total value, used to scale graph
     * @param ar the double array to find total of
     * @return the total value found
     */
    private double getTotal(double[] ar) {
        double total = 0;
        
        for(double val: ar) 
            total = val;
        
        return total;
    }
    
    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        x_scale = (getWidth()/x_total) * 0.98;
        y_scale = (getHeight()/y_max)/2; 
        g2.setColor(java.awt.Color.red);
//        g2.fillRect(0, 0, getWidth(), getHeight());
        int last_x = 0;
        int last_y = getHeight()/2;
        
        for(int i = 0; i < x_coords.length; ++i) {
            int x = (int)(x_coords[i] * x_scale);
            int y = (int)(y_max - y_coords[i] * y_scale/2) + getHeight()/2;
            
            g2.drawLine(x, y, last_x, last_y);
            
            last_x = x;
            last_y = y;
        }
    }
}