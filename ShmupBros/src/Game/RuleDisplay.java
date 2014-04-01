package Game;

import Ai.Rule;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
    
    private int index = -1;
    
    /**
     * Creates new form RuleDisplay
     */
    public RuleDisplay() {
        super();        
        setRule(Settings.rules.get(0));
        
        this.addMouseListener(new MouseListener(){
            private double x_move;
            private double y_move;

            @Override
            public void mouseClicked(MouseEvent e) {
                x_move = e.getX()/x_scale;
                y_move = (y_max - e.getY()/y_scale)*2;
                
                for(int i = 0; i < x_coords.length; ++i) {
                    if(x_coords[i] + 5 > x_move && x_coords[i] - 5 < x_move && 
                            y_coords[i] + 0.2 > y_move && y_coords[i] - 0.2 < y_move) {
                        index = i;
                        break;
                    }                            
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                x_move = e.getX()/x_scale;
                y_move = (y_max - e.getY()/y_scale)*2;
                
                for(int i = 0; i < x_coords.length; ++i) {
                    if(x_coords[i] + 5 > x_move && x_coords[i] - 5 < x_move && 
                            y_coords[i] + 0.2 > y_move && y_coords[i] - 0.2 < y_move) {
                        index = i;
                        break;
                    }                            
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                x_move = e.getX()/x_scale;
                y_move = (y_max - e.getY()/y_scale)*2;
                
                if(index != 0 && index != x_coords.length - 1)
                    x_coords[index] = Math.min(Math.max(x_move, 0), x_total);
                
                y_coords[index] = Math.min(Math.max(y_move, 0), y_max);
                
                RuleDisplay.this.revalidate();
                RuleDisplay.this.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
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
        g2.setColor(new Color(0.75f,0f,0f));
        
        int last_x = (int)(x_coords[0] * x_scale) + 4;
        int last_y = (int)(y_max - y_coords[0] * y_scale/2) + getHeight()/2;
        
        if(index == 0) 
            g2.setColor(new Color(0f,0f,0.75f));
        
        g2.fillOval(last_x-4, last_y-4, 8, 8);
        
        for(int i = 1; i < x_coords.length; ++i) {
            int x = (int)(x_coords[i] * x_scale) + 4;
            int y = (int)(y_max - y_coords[i] * y_scale/2) + getHeight()/2;
            
            g2.setColor(new Color(0.75f,0f,0f));
            g2.drawLine(x, y, last_x, last_y);
            
            if(index == i) 
                g2.setColor(new Color(0f,0f,0.75f));
            
            g2.fillOval(x-4, y-4, 8, 8);
            
            last_x = x;
            last_y = y;
        }
    }
}