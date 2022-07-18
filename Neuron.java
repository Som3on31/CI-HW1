/*
 * @author AR
 * 
 * <p>This class is created to make a full use of the concept of a neuron as a unit
 * </p>
 */
import java.util.Random;


public class Neuron {

    private Neuron[] inputNeurons;
    private float[] rawInput;
    private int inputCount;
    private int MAX_SIZE;

    private float bias;
    private Random rng;
    // private int weight;

    public Neuron(int inputCount, boolean rawInputAllowed, int bias) {
        if (rawInputAllowed)
            rawInput = new float[inputCount];
        else
            inputNeurons = new Neuron[inputCount];
        MAX_SIZE = inputCount;
        this.inputCount = 0;
        MAX_SIZE = inputCount;

        this.bias = bias;

        rng = new Random();
    }

    public float getOutput() {
        float totalValue = 0;
        if (rawInput != null)
            for (float value : rawInput)
                totalValue += value;

        if (inputNeurons != null)
            for (Neuron neuron : inputNeurons)
                totalValue += neuron.getOutput();

        return totalValue + bias;
    }

    public boolean addInput(float input) {
        if (rawInput == null) {
            System.out.println("Error: this neuron accepts neurons as an input only.");
            return false;
        }
        if (!isFull()) {
            rawInput[inputCount] = input;
            inputCount++;

            System.out.println("Successfully added " + input);
            return true;
        }
        System.out.println("Error: The input array is full.");
        return false;
    }

    public boolean addInput(Neuron input) {
        if (rawInput == null) {
            System.out.println("Error: this neuron accepts raw inputs as an input only.");
            return false;
        }
        if (!isFull()) {
            inputNeurons[inputCount] = input;
            inputCount++;

            System.out.println("Successfully added " + input);
            return true;
        }
        System.out.println("Error: The input array is full.");
        return false;
    }

    public boolean isEmpty() {
        return inputCount == 0;
    }

    public boolean isFull() {
        return inputCount == MAX_SIZE;
    }
}