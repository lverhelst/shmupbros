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
    /***
     * Calculates the value of a centroid
     *  raw_and_fuzzy[X][0] => raw value (ie distance from bot to target. EX: 130)
     *  raw_and_fuzzy[X][1] => fuzzy value (ie fuzzyClose(130) => 0.12)
     * @param raw_and_fuzzy_values
     * @return 
     */
    public static double centroidDeFuzzy(double[][] raw_and_fuzzy_values){
        if(raw_and_fuzzy_values.length < 1)
            return -1.0;
        if(raw_and_fuzzy_values[0].length != 2)
            return -1.0;
        //
        double denominator = 0.0;
        double numerator = 0.0;
        
        for(double[] raw_f : raw_and_fuzzy_values){
            denominator += (raw_f[0] * raw_f[1]);
            numerator += raw_f[1];
        }
        
        return denominator / numerator;
    }
    
}
