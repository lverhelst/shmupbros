package Ai;

/**
 * This class is used to perform fuzzy logic on fuzzy values
 * @author Emery Berg and Leon Verhelst
 */
public class FuzzyLogic {
    
    public static double fuzzyAND(double a, double b){
        return Math.min(a, b);
    }
    
    public static double fuzzyOR(double a, double b){
        return Math.max(a, b);
    }
    
    public static double fuzzyNOT(double a){
        return 1.0 - a;
    }
    
    
}
