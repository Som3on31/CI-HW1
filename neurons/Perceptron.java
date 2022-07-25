package neurons;

public interface Perceptron{
    boolean addInput(Perceptron neuron);
    boolean addInput(float input);
    float getOutput();

    boolean isEmpty();
    boolean isFull();

    /**
     * Returns error
     * @param expected
     * @return 
     */
    float getError(float result,float expected);

    float useActivationFn(float output);
}