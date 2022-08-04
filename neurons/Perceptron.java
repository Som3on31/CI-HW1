package neurons;

import java.util.LinkedList;

public interface Perceptron{
    boolean addInput(Perceptron neuron);
    boolean addInput(double input);
    double changeInput(int pos,double newInput);
    double getOutput();
    double getOutputRaw();

    boolean isEmpty();
    /**
     * Checks if there is a space available for more inputs
     * @return a boolean when the condition is met.
     */
    boolean isFull();

    /**
     * Returns error
     * @param expected a value one wishes to see
     * @param result a value gathered from using a perceptron
     * @return a number of double number in (WIP)
     */
    double getError(double result,double expected);

    /**
     * This function is used to determine the final result of an output from a perceptron
     * @param output a value gathered from using a perceptron
     * @return
     */
    double useActivationFn(double output);
    double useDerivFn(double output);

    void updateWeight(int pos,double newValue);
    void updateBias(double newValue);

    LinkedList<Double> weights();
    double bias();
    double lr();

    double mr();
}