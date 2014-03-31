
package Ai;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * @author Leon I. Verhelst
 */
public class Rule {
    private String name;
    public double w;
    private double[] x_coord;
    private double[] y_coord;
    
    public static double defuzzifyRule(Rule r){
        double denominator = 0;
        double numerator = 0;
        for(int i = 0; i < r.getX_coord().length; i++){
            denominator += r.getX_coord()[i] * r.getY_coord()[i];
            numerator += r.getY_coord()[i];
        }
        return denominator/numerator;
    }
    
    
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
        if(value < getX_coord()[0])
            return getY_coord()[0];
        //value is on high end
        if(value >= getX_coord()[getX_coord().length - 1])
            return getY_coord()[getY_coord().length -1];
        //value in middle
        for(int i = 0; i < getX_coord().length - 1; i++){
            //find where value between two points
            if(value >= getX_coord()[i] && value <= getX_coord()[i+1]){
                //y = m x + b )
                //check for vertical line (undef -> defaults to earliest)
                if(getX_coord()[i] == getX_coord()[i+1])
                    return getY_coord()[i];
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
        return (getY_coord()[i+1] - getY_coord()[i])/(getX_coord()[i + 1] - getX_coord()[i]);
    }
    
    /**
     * Calculate the yintercept
     * @param i The first coordinate of the line
     * @return y-intercept
     */
    private double yintercept(int i){
        //y = m x + b
        //y - m x = b
        return getY_coord()[i] - slope(i) * getX_coord()[i];
    }
    
    /**
     * Use Mamdani's MIN to get the consequent (truncate the rule) 
     * @param value
     * @return A new truncated rule
     */
    public Rule applyImplication(double value){
        double[] newY = Arrays.copyOf(getY_coord(), getY_coord().length);
        for(int i = 0; i < newY.length; i++){
            if(newY[i] > value)
                newY[i] = value;
        }
        return new Rule("_" + this.name, getX_coord(), newY);   
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
        
      
            
        while(thisind < getX_coord().length - 1 &&  otherind < otherRule.getX_coord().length -1){
            //check intercepts
            double x1 = getXIntercept(getX_coord()[thisind], getX_coord()[thisind + 1],
                        getY_coord()[thisind], getY_coord()[thisind + 1],
                    otherRule.getX_coord()[otherind], otherRule.getX_coord()[otherind + 1],
                    otherRule.getY_coord()[otherind], otherRule.getY_coord()[otherind + 1]);
            if(x1 != -1.0){
                System.out.println("Intersected");
                addLargest(this, thisind, otherRule, otherind, x, y);
                //did intersect
                double m = (getY_coord()[thisind + 1] - getY_coord()[thisind])/(getX_coord()[thisind + 1] - getX_coord()[thisind]);
                double b = getY_coord()[thisind] - m * getX_coord()[thisind];
                double y1 =  m * x1 + b;
                
                if(Double.isNaN(x1)){
                    x1 = 0.0;
                }
                if(Double.isNaN(y1))
                    y1 = 0.0;
                
                x.add(x1);
                
                y.add(y1);
                
                thisind++;
                otherind++;
                addLargest(this, thisind, otherRule, otherind, x, y);
                
            }else{
                //chose rule with biggest next y
                //add to results list
                addLargest(this, thisind, otherRule, otherind, x, y);
                thisind++;
                otherind++;
                addLargest(this, thisind, otherRule, otherind, x, y);
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
    
    private void addLargest(Rule r1, int index1, Rule r2, int index2, ArrayList<Double> x, ArrayList<Double> y){
        //compare
        //chose rule with biggest first y
        //add to results list
        if(getY_coord()[index1] >= r2.getY_coord()[index2]){
            if(!x.contains(x_coord[index1])){
                x.add(getX_coord()[index1]);
                y.add(getY_coord()[index1]);
            }
        }else if(getY_coord()[index1] < r2.getY_coord()[index2]){
            if(!x.contains(x_coord[index1])){
                x.add(r2.getX_coord()[index2]);
                y.add(r2.getY_coord()[index2]);
            }
        }
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
     * @return the x_coord
     */
    public double[] getX_coord() {
        return x_coord;
    }

    /**
     * @return the y_coord
     */
    public double[] getY_coord() {
        return y_coord;
    }
    
    /**
     * @return String of the values contained in the rule
     */
    @Override public String toString() {
        String temp = "";
        
        for(int i = 0; i < getX_coord().length; i++){
            temp += getX_coord()[i] + "," + getY_coord()[i];
            
            if(i < getX_coord().length - 1)
                temp += "\n";
        }
        
        return temp;
    }
}
