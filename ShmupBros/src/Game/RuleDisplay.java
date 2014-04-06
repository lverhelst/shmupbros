package Game;

import Ai.FuzzySet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

/**
 * @author Emery
 */
public class RuleDisplay extends javax.swing.JPanel {
    private FuzzySet rule;
    private double[] x_coords;
    private double[] y_coords;
    private double x_max;
    private double x_min;
    private double x_scale;
    private double y_scale;
    
    private int index = -1;
    
    /**
     * Creates new form FuzzySetDisplay
     */
    public RuleDisplay() {
        super();        
        setRule(Settings.sets.get(0));
        
        this.addMouseListener(new MouseListener(){
            private double x_move;
            private double y_move;

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                x_move = ((e.getX())/x_scale) + x_min;
                y_move = (1 - e.getY()/y_scale)*2;
                
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
                if(index != -1) {
                    x_move = ((e.getX())/x_scale) + x_min;
                    x_move = Math.min(Math.max(x_move, x_min), x_max);
                    y_move = (1 - e.getY()/y_scale)*2;
                    y_move = Math.min(Math.max(y_move, 0), 1);

                    if((index + 1 < x_coords.length && x_move < x_coords[index + 1]) &&
                            (index - 1 >= 0 && x_move > x_coords[index - 1])) {
                        x_coords[index] = x_move;
                    }
                    y_coords[index] = y_move;

                    RuleDisplay.this.revalidate();
                    RuleDisplay.this.repaint();
                }
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
    public void changeRule(FuzzySet rule) {  
        setRule(rule);
        setBounds(x_coords);
    }
    
    /**
     * Used to set the number of points in the graph
     * @param points an int of the number of point
     */
    public void setPoints(int points) {
        if(points !=  x_coords.length) {
            double[] x_new = new double[points];
            double[] y_new = new double[points];

            for(int i = 0; i < points; ++i) {
                x_new[i] = ((x_max - x_min)/points * i);
                if(i < x_coords.length) {
                    y_new[i] = y_coords[i];
                } else {                
                    y_new[i] = 1;
                }
            }

            x_coords = x_new;
            y_coords = y_new;

            rule.changeSet(x_coords, y_coords);
        }
        
        setBounds(x_coords);
    }
    
    /**
     * private method which does the work of setting a rule
     * @param rule the rule to set
     */
    private void setRule(FuzzySet rule) {  
        this.rule = rule;
        x_coords = rule.getX_coord();
        y_coords = rule.getY_coord();        
        
        setBounds(x_coords);
    }
    
    /**
     * Private method for finding the max value, used to scale graph
     * @param ar the double array to find max of
     * @return the max value found
     */
    private void setBounds(double[] ar) {
        double max = 0;
        double min = 0;
        
        for(double val: ar) {
            if(val > max)
                max = val;
            
            if(val < min)
                min = val;            
        }
        
        x_max = max;
        x_min = min;
        
        revalidate();
        repaint();
    }
    
    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        DecimalFormat formatter =  new DecimalFormat("#.##");
        
        
        x_scale = (getWidth()/(x_max - x_min)) * 0.98;
        y_scale = getHeight()/2; 
        g2.setColor(new Color(0.75f,0f,0f));
        
        int last_x = (int)((x_coords[0] - x_min) * x_scale) + 4;
        int last_y = (int)(1 - y_coords[0] * y_scale/2) + getHeight()/2;
        g2.drawString("[" + formatter.format(x_coords[0]) + "," + formatter.format(y_coords[0]) + "]", last_x, last_y);
        
        if(index == 0) 
            g2.setColor(new Color(0f,0f,0.75f));
        
        g2.fillOval(last_x-4, last_y-4, 8, 8);
        
        for(int i = 1; i < x_coords.length; ++i) {
            int x = (int)((x_coords[i] - x_min)* x_scale) + 4;
            int y = (int)(1 - y_coords[i] * y_scale/2) + getHeight()/2;
            
            g2.setColor(new Color(0.75f,0f,0f));
            g2.drawLine(x, y, last_x, last_y);
            g2.drawString("[" + formatter.format(x_coords[i]) + "," + formatter.format(y_coords[i]) + "]", (x  < this.getWidth() - 40? x : x - 40), y);
            
            if(index == i) 
                g2.setColor(new Color(0f,0f,0.75f));
            
            g2.fillOval(x-4, y-4, 8, 8);
            
            last_x = x;
            last_y = y;
        }
    }
}