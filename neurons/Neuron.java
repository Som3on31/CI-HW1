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
    private LinkedList<Float> rawInputs;
    private int inputCount;
    private final int MAX_SIZE;

    private float bias;
    private static Random rng = new Random();
    private LinkedList<Float> weights;
    private float learningRate;

    public Neuron(int inputCount, boolean rawInputAllowed, int bias, float learningRate) {
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
            weights.add(rng.nextFloat() * minus);
        }

    }

    public Neuron(int inputCount, boolean rawInputAllowed,float weight, int bias, float learningRate) {
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
    public float getOutputRaw() {
        float totalValue = 0;
        if (rawInputs != null)
            for (int i = 0; i < MAX_SIZE; i++)
                totalValue += rawInputs.get(i) * weights.get(i);

        if (inputNeurons != null)
            for (int i = 0; i < MAX_SIZE; i++)
                totalValue += inputNeurons.get(i).getOutput() * weights.get(i);

        return totalValue;
    }

    public float getOutput() {
        return useActivationFn(getOutputRaw() + bias);
//        return getOutputRaw() + bias;
    }


    public float finalizeOutput() {
        return 0f;
    }

    public boolean addInput(float input) {
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

    public float changeInput(int pos,float input){
        float oldValue = rawInputs.get(pos);
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
            for (float input : rawInputs) {
                sb.append(input);
                sb.append(" ");
            }
        }
        sb.append("\n");
        for (float weight : weights) {
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
    public float getError(float result, float expected) {
        return expected - result / expected;
    }

    public float useActivationFn(float input) {
        return Math.max(0, input);
    }

    public int useDerivFn(float input){
        return input > 0f ? 1 : 0;
    }

    public void updateWeight(int pos,float newValue) {
        weights.set(pos,newValue);
    }

    @Override
    public void updateBias(float newValue) {
        
        
    }

    @Override
    public LinkedList<Float> weights() {
        return weights;
    }

    @Override
    public float bias() {
        return bias;
    }

    @Override
    public float lr() {
        return learningRate;
    }

}