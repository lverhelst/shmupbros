package Ai;

/**
 *
 * @author Leon Verhelst
 */
public class FuzzyRule {
            
    /**
     * Returns whether the distance is close
     * @param distance The distance
     * @return numerical value of between 0.0 and 1.0 representing if the distance is not close (0.0) of if the distance is close (1.0) or if the distance is between (0.0, 1.0)
     */
    public static double fuzzyCLOSE(double distance){
        //formula slope (m) = (y1 - y0)/(x1 - x0)
        float slope = (float)(1.0 - 0.0)/(150 - 300);
        //Distance is close
        if(distance < 150)
            return 1;
        //Distance is medium
        if(distance >= 150 && distance <= 300){
            //calculate y = mx + b, b= 1.6666666, m = slope, x = distance
            return (slope * distance) + 2;
        }
        //Distance is far       
        return 0.001;
    }
    /**
     * Returns whether the distance is middle
     * @param distance The distance
     * @return numerical value of between 0.0 and 1.0 representing if the distance is not middle (0.0) of if the distance is middle (1.0) or if the distance is between (0.0, 1.0)
     */
    public static double fuzzyMIDDLE(double distance){
        if(distance < 250)
            return 0;
        if(distance >= 250 && distance < 350){
            return ((1.0 - 0.0)/(350 - 250)) * distance - 2.5;
        }
        if(distance >= 350 && distance <= 500){
            return 1;
        }
        if(distance < 650 && distance > 500){
            return ((1.0 - 0.0)/(500 - 650)) * distance + 4.333333333333334;
        }
        return 0.001;
    }
    
    /**
     * Returns whether the distance is far
     * @param distance The distance
     * @return numerical value of between 0.0 and 1.0 representing if the distance is not far (0.0) of if the distance is far (1.0) or if the distance is between (0.0, 1.0)
     */
    public static double fuzzyFAR(double distance){
        if(distance > 750)
            return 1.0;
        if(distance <= 750 && distance > 600){
            return ((0.0 - 1.0)/(600 - 750)) * distance - 4;
        }
        return 0.001;
    }
    
    /**
     * Returns the amount of force to exert on the object at a certain distance
     * @param distance double distance
     * @return Force as double
     */
    public static double forceFromDistance(double distance){
        if(FuzzyLogic.fuzzyOR(fuzzyFAR(distance), fuzzyMIDDLE(distance)) > 0.25){
            return 8.0;
        }else if(fuzzyCLOSE(distance) == 1.0){
            return 3.0;
        }else{
            return 1;
        }
    }
    
    /**
     * Returns a discrete value if the angle is facing 0
     * Metrics:
     *      (-15, 15) = 1.0
     *      [-45, -15][15, 45] = 1.0 -> 0.0
     *      [-45, -180][45, 180] = 0.0
     * 
     * Shape:
     *      ___________/------\_______
     * 
     * @param angle
     * @return 
     */
    public static double fuzzyFACING(double angle){
        if(angle >= -15 && angle <= 15)
            return 1.0;
        //downslope
        if(angle > -45 && angle < -15){
            //y = mx + b
            return (0.0 - 1.0)/(-45 - -15) * angle + 1.5;
        }
        else if(angle > 15 && angle < 45){
            //y = mx + b
            return (1.0 - 0.0)/(15 - 45) * angle + 1.5;
        }    
        else
            return 0.0;
    }
    /**
     * Returns if the angle denotes a small turn
     * 
     * Values
     *      [-30,30] = 0.0
     *      [-50,-30][30,50] = 0.0 -> 1.0
     *      [-90,50][50,90] =  1.0
     *      [-110,-90][90,110] = 1.0 -> 0.0
     *      [-180,-110][110,180] = 0.0
     *
     * Shape:
     * ______/-------\_______/-------\________
     * @param angle
     * @return 
     */
    public static double fuzzySMALLTURN(double angle){
        angle = Math.abs(angle);
        
        if(angle >= 0 && angle <= 30)
            return 0;
        
        if(angle < 50 && angle > 30)
            return (0.0 - 1.0)/(30 - 50) * angle + - 1.5;
        
        if(angle >= 50 && angle <= 90)
            return 1.0;
       
        if(angle > 90 && angle < 110)
            return (1.0 - 0.0)/(90 - 110) * angle + 5.5;
            
        return 0.0;    
    }
    /**
     * Returns if the turn is a big turn;
     * 
     * Values:
     *  [-80, 80] = 0.0;
     *  [-120,-80][80,120] = 0.0 -> 1.0
     *  [-180,-120][120,180] = 1.0
     * 
     * @param angle 
     * @return 
     */
    public static double fuzzyBIGTURN(double angle){
        angle = Math.abs(angle);
        if(angle <= 80)
            return 0.0;
        
        if(angle > 80 && angle < 120)
            return (0.0 - 1.0)/(80 - 120) * angle + - 2.0;
        
        return 1.0;
    }
    /**
     * Returns result of all fuzzy rotation items
     * @param angle
     * @return 
     */
    public static double fuzzyRotation(double angle){
        double a = fuzzyFACING(angle);
        double b = fuzzySMALLTURN(angle);
        double c = fuzzyBIGTURN(angle);
        
        return FuzzyLogic.fuzzyOR(a, FuzzyLogic.fuzzyOR(b, c));
    }
    
}
