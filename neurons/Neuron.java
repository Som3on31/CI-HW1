package neurons;

import java.util.Random;
import java.util.LinkedList;

public class Neuron implements Perceptron {

    private LinkedList<Perceptron> inputNeurons;
    private LinkedList<Double> rawInputs;
    private int inputCount;
    private final int MAX_SIZE;

    private double bias;
    private static Random rng = new Random();
    private LinkedList<Double> weights;
    private double learningRate;
    private double momentumRate;

    public Neuron(int inputCount, boolean rawInputAllowed, int bias, double learningRate, double momentumRate) {
        if (rawInputAllowed)
            rawInputs = new LinkedList<>();
        else
            inputNeurons = new LinkedList<>();
        MAX_SIZE = inputCount;
        this.inputCount = 0;

        this.bias = bias;
        weights = new LinkedList<>();

        this.learningRate = learningRate;
        this.momentumRate = momentumRate;

        for (int i = 0; i < inputCount; i++) {
            int minus = rng.nextInt(100) > 49 ? 1 : -1;
            weights.add(rng.nextDouble() * minus);
        }

    }

    public Neuron(int inputCount, boolean rawInputAllowed, double weight, int bias, double learningRate, double momentumRate) {
        if (rawInputAllowed)
            rawInputs = new LinkedList<>();
        else
            inputNeurons = new LinkedList<>();
        MAX_SIZE = inputCount;
        this.inputCount = 0;

        this.bias = bias;
        weights = new LinkedList<>();

        this.learningRate = learningRate;
        this.momentumRate = momentumRate;

        for (int i = 0; i < inputCount; i++) {
            weights.add(weight);
        }
    }

    @Override
    public double getOutputRaw() {
        double totalValue = 0;
        if (rawInputs != null)
            for (int i = 0; i < MAX_SIZE; i++)
                totalValue += rawInputs.get(i);

        if (inputNeurons != null)
            for (int i = 0; i < MAX_SIZE; i++)
                totalValue += inputNeurons.get(i).getOutput() * weights.get(i);

        return totalValue;
    }

    public double getOutput() {
        return useActivationFn(getOutputRaw() + bias);
//        return getOutputRaw() + bias;
    }

    @Override
    public boolean addInput(double input) {
        if (rawInputs == null) {
            System.out.println("Error: this neuron accepts neurons as an input only.");
            return false;
        }
        if (!isFull()) {
            rawInputs.add(input);
            inputCount++;

//            System.out.println("Successfully added " + input);
            return true;
        }
        System.out.println("Error: The input array is full.");
        return false;
    }

    @Override
    public boolean addInput(Perceptron input) {
        if (inputNeurons == null) {
            System.out.println("Error: this neuron accepts raw inputs as an input only.");
            return false;
        }
        if (!isFull()) {
            inputNeurons.add(input);
            inputCount++;

//            System.out.println("Successfully added " + input);
            return true;
        }
        System.out.println("Error: The input array is full.");
        return false;
    }

    @Override
    public double changeInput(int pos, double input) {
        double oldValue = rawInputs.get(pos);
        rawInputs.set(pos, input);
        return oldValue;
    }

    /**
     * Checks if there is currently no input at the moment.
     */
    public boolean isEmpty() {
        return inputCount == 0;
    }

    /**
     * Checks if a neuron cannot accpet any more input.
     */
    public boolean isFull() {
        return inputCount == MAX_SIZE;
    }

    /**
     * (Deprecated) Returns a how close a value of the result to a desired value
     * @param result a value gathered from using a perceptron
     * @param expected a value one wishes to see
     * @return a value in (expected - result) / expected
     */
    @Override
    @Deprecated
    public double getError(double result, double expected) {
        return (expected - result) / expected;
    }

    @Override
    public double useActivationFn(double input) {
        return Math.max(0.01 * input, input);
    }

    @Override
    public double useDerivFn(double input) {
        return input > 0 ? 1 : 0.001;
    }

    /**
     *
     * @param pos weight j that connects to this neuron
     * @param newValue a new weight to be used
     */
    @Override
    public void updateWeight(int pos, double newValue) {
        weights.set(pos, newValue);
    }

    /**
     * Changes the current bias to a new value
     * @param newValue a new bias to be used
     */
    @Override
    public void updateBias(double newValue) {
        bias =  newValue;
    }

    /**
     * Returns a linked list of weights connected to this neuron.
     * @return a linked list which contains a set of weights that connect to this neuron
     */
    @Override
    public LinkedList<Double> weights() {
        return weights;
    }

    /**
     * Returns bias of the neuron
     * @return bias used from initialization
     */
    @Override
    public double bias() {
        return bias;
    }

    /**
     * Returns learning rate of the neuron
     * @return learning rate used from initialization
     */
    @Override
    public double lr() {
        return learningRate;
    }

    /**
     * Returns momentum rate of the neuron
     * @return momentum rate used from initialization
     */
    @Override
    public double mr() { return momentumRate; }

}