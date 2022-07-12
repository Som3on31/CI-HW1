package customlib;

/*
 * This custom class makes use of a pseudo random number generator called
 * in order to achieve the goal of using purely all custom-made library.
 */


public class CustomRandom{

    /*
     * Here are initilized numbers, there are seed and max value allowed for integer
     * seed is used to generate a number.
     * MAX_INTEGER exists to be used when a user desires to get a specific value as a means to
     * limit the range.
     */
    private static int seed = 23543;
    private static final int MAX_INTEGER = 2147483647;
    
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

    public static int rand(int range){
        return range*rand()/MAX_INTEGER;
    }
}