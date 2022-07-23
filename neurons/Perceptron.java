package neurons;

public interface Perceptron{
    boolean addInput(Perceptron neuron);
    boolean addInput(float input);
    float getOutput();

    boolean isEmpty();
    boolean isFull();
    float getError();
}