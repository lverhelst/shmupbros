package Ai;

import Game.State.GameState;
import java.util.ArrayList;

/**
 * Used to represent the fuzzy rules
 * @author Emery
 */
public class FuzzyRule {
    private ArrayList<String> setList;
    private ArrayList<String> varList;
    private ArrayList<String> opList;
    private FuzzySet then;
    
    //distance vars and weight
    private double distance, distance2, weight;
    //turn vars
    private double left, facing, right;
    //angle vars
    private double small, normal, large;
    
    //local calc var
    private double x;
    
    //list of rules in order
    String[] ruleList;
    String name;
    
    /**
     * Used to create the base of the rule
     * @param set the base fuzzy set name
     * @param var the variable to use
     */
    public FuzzyRule(String set, String var, FuzzySet then) {
        setList = new ArrayList();
        varList = new ArrayList();
        opList = new ArrayList();
        setList.add(set);
        varList.add(var);
        this.then = then;
    }
    
    public FuzzyRule(String rawRule){
        name = rawRule.substring(rawRule.indexOf(":") + 1, rawRule.indexOf("{"));
        String rawlist = rawRule.substring(rawRule.indexOf("{") + 1, rawRule.indexOf("}"));
        ruleList = rawlist.split("!");                       
    }
    
    public FuzzySet evalRule(){
        x = 0;
        for(String r : ruleList){
            x = evalAntecedant(r);
        }
        FuzzySet slow = GameState.getRule(name);
        
        return slow.applyImplication(x * weight);
    }
    
    public double evalAntecedant(String smallrule){
        //base case
        int count = 0;
        for(int i = 0; i < smallrule.length(); i++){
            if(smallrule.charAt(i) == '(')
                count++;
        }
        
        if(count == 0){
            String begin = smallrule.substring(0, smallrule.indexOf(":"));
            String end = smallrule.substring(smallrule.indexOf(":") + 1, smallrule.length());
            //System.out.println(smallrule + " No brackets: " +  (double)getFuzzyValue(begin, end));
            
            return getFuzzyValue(begin, end);
        }  
        else{           
            //get operator
            String op = smallrule.substring(0, smallrule.indexOf("("));
            smallrule = smallrule.substring(smallrule.indexOf("(") + 1, smallrule.length() - 1);
           
            if(op.equals("NOT")){
                 double xy = applyOperator(evalAntecedant(smallrule.substring(0, smallrule.length())) ,0, op);
                 // System.out.println("NOT " +  xy);
                     return xy;
            }
            else{
                //and /or
                double a = evalAntecedant(smallrule.substring(0, smallrule.indexOf(",")));
                double b = evalAntecedant(smallrule.substring(smallrule.indexOf(",") + 1, smallrule.length()));
                double xz = applyOperator(a,b,op);
               // System.out.println("a " +  a + " b " + b + " " + xz);
                return xz;
            }
        }
    }
 
    /**
     * Used to define the then set
     * @param set 
     */
    public void thenFuzzySet(FuzzySet set) {
        then = set;
    }
    
    /**
     * Used to add a rule with a operator associated
     * @param operator the operator to sue
     * @param var the variable to use (distance, distance2, angleTarget, angleNode)
     * @param set the fuzzy set to use
     */
    public void addFuzzySet(String operator, String set, String var) {
        setList.add(set);
        opList.add(operator);
        varList.add(var);
    }
    
    /**
     * Used to set the distance vars to use
     * @param distance the distance cast from ray 1
     * @param distance2 the distance cast from ray 2
     */
    public void setDistance(double distance, double distance2) {
        this.distance = distance;
        this.distance2 = distance2;
    }
    
    /**
     * The direction to turn
     * @param left the value of left
     * @param facing the value of facing
     * @param right the value of right
     */
    public void setTurn(double left, double facing, double right) {
        this.left = left;
        this.facing = facing;
        this.right = right;
    }
    
    /**
     * Used to set the angle the bot wants to turn
     * @param small the value of small angle
     * @param normal the value of normal angle
     * @param large the value of large angle
     */
    public void setAngle(double small, double normal, double large) {
        this.small = small;
        this.normal = normal;
        this.large = large;
    }
    
    /**
     * Used to set the then result weight
     * @param weight the weight used in then exp
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    /**
     * Used to evaluate the if - then rule
     * @return the resulting value
     */
    public FuzzySet evaluate() {
        double result = getFuzzyValue(setList.get(0), varList.get(0));
        
        for(int i = 1; i < setList.size(); ++i) {
                result = applyOperator(getFuzzyValue(setList.get(i), varList.get(i)), result, opList.get(i-1));
        }   
        return then.applyImplication(result * weight);
    }
    
    /**
     * Used to get the needed fuzzy set value
     * @param set the name of the fuzzy set
     * @param var the variable to use to evaluate
     * @return the fuzzy set value by that name
     */
    public double getFuzzyValue(String set, String var) {
        double value = getVariable(var);

        if(!set.equals("[VAR]")) {
            FuzzySet fuzzySet = GameState.getRule(set);
            double xa = fuzzySet.evaluate(value);
            //if(xa > 0 && xa < 1)
              //  System.err.println(xa + " " + value + " " + set + " var "  +var );
            return xa;
        }
        
        return value;
    }
    
    /**
     * Used to retrieve the needed values
     * @param var the name of the variable to retrieve
     * @return the value as a double
     */
    public double getVariable(String var) {
        switch(var) {
            case "Ray":
                return distance;
            case "Ray1":
                return distance2;
            case "Left":
                return left;
            case "Facing":
                return facing;
            case "Right":
                return right;
            case "Small Angle":
                return small;
            case "Normal Angle":
                return normal;
            case "Large Angle":
                return large;
            case "x":
                return  x;
        }
        
        return 0;
    }
    
    /**
     * Used to apply operators to the rules
     * @param result the result of evaluating value
     * @param result2 the result of evaluating value 2
     * @param operator the operator to use (AND, OR, NOT)
     * @return the resulting value
     */
    private double applyOperator(double result, double result2, String operator) {        
        switch(operator) {
            case "AND":
                return FuzzyOperator.fuzzyAND(result, result2);
            case "OR":
                return FuzzyOperator.fuzzyOR(result, result2);
            case "NOT":
                return FuzzyOperator.fuzzyNOT(result);
        }
        
        return 0;
    }
    
    @Override public String toString() {
        String temp = "Rule:" + name + "{";
        
        for(int i = 0; i < ruleList.length; ++i) {
            temp += ruleList[i];   
            
            if(i < ruleList.length - 1)
                temp += "!";
        }
        
        return temp + "}";
    }
}