package neurons;

public interface Perceptron{
    boolean addInput(Perceptron neuron);
    float getOutput();

    boolean isEmpty();
    boolean isFull();
}