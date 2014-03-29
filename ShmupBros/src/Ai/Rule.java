
package Ai;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * @author Leon I. Verhelst
 */
public class Rule {
    private String name;
    private double[] x_coord;
    private double[] y_coord;
    
    /**
     * Default constructor for a fuzzy rule, both arrays must be same length
     * @param name the name of the rule
     * @param xs the x positions
     * @param ys the y value at x positions
     */
    public Rule(String name, double[] xs, double[]ys){
        if(xs == null || ys == null || xs.length != ys.length)
            System.err.println("Invalid rule. Coordinate Values must mtch and cannot be null");
        
        this.name = name;
        x_coord = xs;
        y_coord = ys;
    }
    
    /**
     * Used to evaluate the fuzzy membership value at the passed in point
     * @param value the number to check the fuzzy membership value
     * @return values fuzzy membership value
     */
    public double evaluate(double value){
        double result = -1.0;
        //value is on lower end
        if(value < x_coord[0])
            return y_coord[0];
        //value is on high end
        if(value >= x_coord[x_coord.length - 1])
            return y_coord[y_coord.length -1];
        //value in middle
        for(int i = 0; i < x_coord.length - 1; i++){
            //find where value between two points
            if(value >= x_coord[i] && value <= x_coord[i+1]){
                //y = m x + b )
                //check for vertical line (undef -> defaults to earliest)
                if(x_coord[i] == x_coord[i+1])
                    return y_coord[i];
//                System.out.println(slope(i) + "  " + yintercept(i));
                return value * slope(i) + yintercept(i);
            }
            
        }
        return result;
    }
    
    /***
     * Calculates the slope of a line
     * @param i I -> starting index of the line, ending index is i+1
     * @return slope
     */
    private double slope(int i){
        return (y_coord[i+1] - y_coord[i])/(x_coord[i + 1] - x_coord[i]);
    }
    
    /**
     * Calculate the yintercept
     * @param i The first coordinate of the line
     * @return y-intercept
     */
    private double yintercept(int i){
        //y = m x + b
        //y - m x = b
        return y_coord[i] - slope(i) * x_coord[i];
    }
    
    /**
     * Use Mamdani's MIN to get the consequent (truncate the rule) 
     * @param value
     * @return A new truncated rule
     */
    public Rule applyImplication(float value){
        double[] newY = Arrays.copyOf(y_coord, y_coord.length);
        for(int i = 0; i < newY.length; i++){
            if(newY[i] > value)
                newY[i] = value;
        }
        return new Rule("_" + this.name, x_coord, newY);   
    }
    
    /**
     * returns the rule created by overlaying a rule atop this rule 
     * Uses Mamdani's MAX principle
     * @param otherRule
     * @return 
     */
    public Rule aggregate(Rule otherRule){
        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();
        
        int thisind = 0;
        int otherind = 0;
        
        //chose rule with biggest first y
        //add to results list
        if(y_coord[thisind] > otherRule.y_coord[otherind]){
            otherind++;
            x.add(x_coord[thisind]);
            y.add(y_coord[thisind]);
        }else if(y_coord[thisind] < otherRule.y_coord[otherind]){
            thisind++;
            x.add(otherRule.x_coord[otherind]);
            y.add(otherRule.y_coord[otherind]);
        }else{
            thisind++;
            otherind++;
            x.add(x_coord[thisind]);
            y.add(y_coord[thisind]);
        }
            
        while(thisind < x_coord.length - 1 &&  otherind < otherRule.x_coord.length -1){
            
            
            
            //check intercepts
            double x1 = getXIntercept(x_coord[thisind], x_coord[thisind + 1],
                        y_coord[thisind], y_coord[thisind + 1],
                    otherRule.x_coord[otherind], otherRule.x_coord[otherind + 1],
                    otherRule.y_coord[otherind], otherRule.y_coord[otherind + 1]);
            if(x1 != -1.0){
                System.out.println("INtersecte");
                
                //did intersect
                double m = (y_coord[thisind + 1] - y_coord[thisind])/(x_coord[thisind + 1] - x_coord[thisind]);
                double b = y_coord[thisind] - m * x_coord[thisind];
                
                double y1 =  m * x1 + b;
                x.add(x1);
                y.add(y1);
                
                thisind++;
                otherind++;
                
                
            }else{
                  //chose rule with biggest next y
                    //add to results list
                
                    System.out.println("! " +y_coord[thisind] + " " + otherRule.y_coord[otherind]);
                    
                    if(y_coord[thisind] >= otherRule.y_coord[otherind]){
                        otherind++;
                        x.add(x_coord[thisind]);
                        y.add(y_coord[thisind]);
                    }else{
                        thisind++;
                        x.add(otherRule.x_coord[otherind]);
                        y.add(otherRule.y_coord[otherind]);
                    }
            }
        }
        
        //save to arrays
        double[] retx = new double[x.size()];
        for(int i = 0; i < x.size(); i++)
            retx[i] = x.get(i);
        
        double[] rety = new double[y.size()];
        for(int i = 0; i < y.size(); i++)
            rety[i] = y.get(i);
        
        return new Rule(this.name + otherRule.name, retx, rety);
    }
    
    private double getXIntercept(double x1, double x2, double y1, double y2, double x3, double x4, double y3, double y4){
        //if either is vertical
        if(x1 == x2 || x3 == x4){
            if(x1 != x3)
                return -1.0; // no intersection
            else{
                 return x1; //lines are on top of eachther
            }
        }
        
        double a1 = (y2-y1)/(x2-x1);
        double b1 = y1 - a1*x1 ;
        double a2 = (y4-y3)/(x4-x3);
        double b2 = y3 - a2*x3;
        //parrallel, doesn't really matter
        if(a1 == a2)
            return -1;
        double x0 = -(b1 - b2)/(a1-a2);
        //ensure x0 is on the line seqments
        if(x0 >= x1 && x0 <= x2 && x0 >= x3 && x0 <= x4)
            return x0;
        else
            return -1.0;
    }
    
    /**
     * @return string of the rules name 
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return String of the values contained in the rule
     */
    @Override public String toString() {
        String temp = "";
        
        for(int i = 0; i < x_coord.length; i++){
            temp += x_coord[i] + "," + y_coord[i];
            
            if(i < x_coord.length - 1)
                temp += "\n";
        }
        
        return temp;
    }
}
