package neurons;

/*
 * @author AR
 * 
 * <p>This class is created to make a full use of the concept of a neuron as a unit
 * </p>
 */
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

    public Neuron(int inputCount, boolean rawInputAllowed, int bias, double learningRate) {
        if (rawInputAllowed)
            rawInputs = new LinkedList<>();
        else
            inputNeurons = new LinkedList<>();
        MAX_SIZE = inputCount;
        this.inputCount = 0;

        this.bias = bias;
        weights = new LinkedList<>();

        this.learningRate = learningRate;

        for (int i = 0; i < inputCount; i++) {
            int minus = rng.nextInt(100) > 49 ? 1 : -1;
            weights.add(rng.nextDouble()  * minus);
        }

    }

    public Neuron(int inputCount, boolean rawInputAllowed,double weight, int bias, double learningRate) {
        if (rawInputAllowed)
            rawInputs = new LinkedList<>();
        else
            inputNeurons = new LinkedList<>();
        MAX_SIZE = inputCount;
        this.inputCount = 0;

        this.bias = bias;
        weights = new LinkedList<>();

        this.learningRate = learningRate;

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


    public double finalizeOutput() {
        return 0f;
    }

    public boolean addInput(double input) {
        if (rawInputs == null) {
            System.out.println("Error: this neuron accepts neurons as an input only.");
            return false;
        }
        if (!isFull()) {
            rawInputs.add(input);
            inputCount++;

            System.out.println("Successfully added " + input);
            return true;
        }
        System.out.println("Error: The input array is full.");
        return false;
    }

    public boolean addInput(Perceptron input) {
        if (inputNeurons == null) {
            System.out.println("Error: this neuron accepts raw inputs as an input only.");
            return false;
        }
        if (!isFull()) {
            inputNeurons.add(input);
            inputCount++;

            System.out.println("Successfully added " + input);
            return true;
        }
        System.out.println("Error: The input array is full.");
        return false;
    }

    public double changeInput(int pos,double input){
        double oldValue = rawInputs.get(pos);
        rawInputs.set(pos, input);
        return oldValue;
    }

    /**
     * Returns a set of data of this neuron. This includes all inputs, weights and
     * bias
     * to be saved for later use and to simulate "learning" ability of the network.
     */
    public void getNeuronData() {
        // String weightData = new String();
        // String biasData = new String();
        // String learningRate = new String();
        StringBuilder sb = new StringBuilder();

        if (rawInputs != null) {
            for (double input : rawInputs) {
                sb.append(input);
                sb.append(" ");
            }
        }
        sb.append("\n");
        for (double weight : weights) {
            sb.append(weight);
            sb.append(" ");
        }
        sb.append("\n" + bias + "\n");

        String result = sb.toString();

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

    @Override
    public double getError(double result, double expected) {
        return expected - result / expected;
    }

    public double useActivationFn(double input) {
        return Math.max(0.001 * input, input);
    }

    public double useDerivFn(double input){
        return input > 0 ? 1 : 0.001;
    }

    public void updateWeight(int pos,double newValue) {
        weights.set(pos,newValue);
    }

    @Override
    public void updateBias(double newValue) {
        
        
    }

    @Override
    public LinkedList<Double> weights() {
        return weights;
    }

    @Override
    public double bias() {
        return bias;
    }

    @Override
    public double lr() {
        return learningRate;
    }

}