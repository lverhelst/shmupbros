package Ai;

import java.util.ArrayList;

/**
 * Used to represent the fuzzy rules
 * @author Emery
 */
public class FuzzyRule {
    private ArrayList<FuzzySet> setList;
    private ArrayList<String> varList;
    private ArrayList<String> opList;
    private FuzzySet then;
    
    //distance vars
    private double distance, distance2;
    //turn vars
    private double left, facing, right;
    //angle vars
    private double small, normal, large;
    
    /**
     * Used to create the base of the rule
     * @param set the base rule
     */
    public FuzzyRule(FuzzySet set, String var) {
        setList = new ArrayList();
        varList = new ArrayList();
        opList = new ArrayList();
        setList.add(set);
        varList.add(var);
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
    public void addFuzzySet(String operator, String var, FuzzySet set) {
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
     * Used to evaluate the if - then rule
     * @return the resulting value
     */
    public FuzzySet evaluate() {
        double var = getVariable(varList.get(0));
        double result = setList.get(0).evaluate(var);
        
        for(int i = 1; i < setList.size(); ++i) {
            var = getVariable(varList.get(i));
            result = applyOperator(setList.get(i).evaluate(var), result, opList.get(i-1));
        }
        
        return then.applyImplication(result);
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
}