package Ai;

/**
 * This class is used to perform fuzzy logic on fuzzy values
 * @author Emery Berg and Leon Verhelst
 */
public class FuzzyOperator {
    
    
    /**
     * Fuzzy AND operation, gives the min of the two values
     * @param a value 1
     * @param b value 2
     * @return the min value
     */
    public static double fuzzyAND(double a, double b){
        return Math.min(a, b);
    }
    
    /**
     * Fuzzy OR operation, gives the max of the two values
     * @param a value 1
     * @param b value 2
     * @return the max value
     */
    public static double fuzzyOR(double a, double b){
        return Math.max(a, b);
    }
    
        /**
     * Fuzzy Not operation, gives the negation of the current value
     * @param a value
     * @return the negated value
     */
    public static double fuzzyNOT(double a){
        return 1.0 - a;
    }
}