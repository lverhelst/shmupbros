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
        if(distance < 300)
            return 1;
        //Distance is medium
        if(distance >= 150 && distance <= 300){
            //calculate y = mx + b, b= 1.6666666, m = slope, x = distance
            return (slope * distance) + 2;
        }
        //Distance is far       
        return 0;
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
        return 0;
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
        return 0;
    }
    
    public static double forceFromDistance(double distance){
        if(FuzzyLogic.fuzzyOR(fuzzyFAR(distance), fuzzyMIDDLE(distance)) > 0.25){
            return 8.0;
        }else if(fuzzyCLOSE(distance) == 1.0){
            return 3.0;
        }else{
            return 1;
        }
    }
    
}
