package neurons;
/*
 * @author AR
 * 
 * <p>This class is created to make a full use of the concept of a neuron as a unit
 * </p>
 */
import java.util.Random;
import java.util.LinkedList;


public class Neuron implements Perceptron{

    private Perceptron[] inputNeurons;
    private float[] rawInput;
    private int inputCount;
    private int MAX_SIZE;

    private float bias;
    private Random rng;
    private float[] weights;

    private float learningRate;
    // private int weight;

    public Neuron(int inputCount, boolean rawInputAllowed, int bias) {
        if (rawInputAllowed)
            rawInput = new float[inputCount];
        else
            inputNeurons = new Neuron[inputCount];
        MAX_SIZE = inputCount;
        this.inputCount = 0;

        this.bias = bias;
        rng = new Random();
        weights = new float[inputCount];

        for(int i=0;i<inputCount;i++) weights[i] = rng.nextInt(10);
    }

    public float getOutput() {
        float totalValue = 0;
        if (rawInput != null)
            for (int i=0;i<MAX_SIZE;i++)
                totalValue += rawInput[i] * weights[i];

        if (inputNeurons != null)
            for (int i=0;i<MAX_SIZE;i++)
                totalValue += inputNeurons[i].getOutput() * weights[i];

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

    public boolean addInput(Perceptron input) {
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