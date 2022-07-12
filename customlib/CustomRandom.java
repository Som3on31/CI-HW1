package customlib;

/*
 * This custom class makes use of a pseudo random number generator called
 * in order to achieve the goal of using purely all custom-made library.
 */


public class CustomRandom{

    private static int seed = 23543;
    
    
    /**
     * gives a random value
     * 
     * @return seed for better 
     */
    public static int rand(){
        return seed;
    }

    public static void setSeed(int customSeed){
        seed = customSeed;
    }
}