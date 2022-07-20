import java.util.LinkedList;

import neurons.Neuron;

public class NeuralNetwork {

    private LinkedList<Neuron> inputs;
    private LinkedList[] hiddenLayers;
    private LinkedList<Neuron> outputs;

    //Maximum size of inputs,hidden layers, neurons per layer and outputs
    int inputSize;
    int hiddenLayerSize;
    int neuronPerHidden;
    int outputSize;

    public NeuralNetwork(int inputNumber, int hiddenLayerCount, int neuronPerLayer, int outputNumber) {
        inputSize = inputNumber;
        this.hiddenLayerSize = hiddenLayerCount;
        neuronPerHidden = neuronPerLayer;
        outputSize = outputNumber;

        inputs = new LinkedList<>();
        hiddenLayers = new LinkedList[hiddenLayerCount];
        for (int i = 0; i < hiddenLayerCount; i++)
            hiddenLayers[i] = new LinkedList<>();

        outputs = new LinkedList<>();
    }

    public boolean addInput(Neuron newInput) {
        if (inputs.size() < inputSize) {
            inputs.add(newInput);
            return true;
        }
        return false;
    }

    public boolean addHidden() {

        return false;
    }

    public boolean addOutput(Neuron newOutput) {
        if (inputs.size() < inputSize) {
            outputs.add(newOutput);
            return true;
        }
        return false;
    }

    public float[] getOutput() {
        float[] results = new float[outputSize + 1];            //Creates an array size of output size plus one for margin error.

        for(int i=0;i<results.length;i++) results[i] = outputs.get(i).getOutput();  //Get results first
        //Then an error percentage.

        return results;
    }
}