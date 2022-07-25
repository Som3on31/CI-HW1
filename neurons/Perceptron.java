package neurons;

public interface Perceptron{
    boolean addInput(Perceptron neuron);
    boolean addInput(float input);
    float changeInput(int pos,float newInput);
    float getOutput();

    boolean isEmpty();
    /**
     * Checks if there is a space available for more inputs
     * @return a boolean when the condition is met.
     */
    boolean isFull();

    /**
     * Returns error
     * @param expected a value one wishes to see
     * @param float a value gathered from using a perceptron
     * @return a number of float number in (WIP)
     */
    float getError(float result,float expected);

    /**
     * This function is used to determine the final result of an output from a perceptron
     * @param output a value gathered from using a perceptron
     * @return
     */
    float useActivationFn(float output);
    int useDerivFn(float output);

    void updateWeight(int layer,int pos,float error,float kek);
}