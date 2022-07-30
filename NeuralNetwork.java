import java.util.LinkedList;
import java.util.Random;

import neurons.*;

public class NeuralNetwork {

    private LinkedList<Perceptron> inputs;
    private LinkedList<Perceptron>[] hiddenLayers;
    private LinkedList<Perceptron> outputs;

    // Maximum size of inputs,hidden layers, neurons per layer and outputs
    private int inputSize;
    private int currentInput;
    private int hiddenLayerSize;
    private int neuronPerHidden;
    private int outputSize;

    @SuppressWarnings("unchecked")
    public NeuralNetwork(int inputNumber, int hiddenLayerCount, int hiddenPerLayer, int outputNumber,
            int learningRate) {
        inputSize = inputNumber;
        currentInput = 0;
        this.hiddenLayerSize = hiddenLayerCount;
        neuronPerHidden = hiddenPerLayer;
        outputSize = outputNumber;

        inputs = new LinkedList<>();

        hiddenLayers = new LinkedList[hiddenLayerCount];
        for (int i = 0; i < hiddenLayerCount; i++)
            hiddenLayers[i] = new LinkedList<>();

        outputs = new LinkedList<>();

        for (int i = 0; i < outputSize; i++) {
            outputs.add(new Neuron(hiddenPerLayer, false, 0, learningRate));
        }

        for (int i = 0; i < inputNumber; i++) {
            inputs.add(new Neuron(1, true, 0, 0));
        }

        for (int i = 0; i < hiddenLayerCount; i++) {
            for (int j = 0; j < neuronPerHidden; j++) {
                boolean nearInputs = i == 0;
                int inputForHidden = nearInputs ? inputNumber : hiddenPerLayer;
                hiddenLayers[i].add(new Neuron(inputForHidden, false, 0, learningRate));
            }
        }

        connect();

        System.out.println("Construction complete");
    }

    @SuppressWarnings("unchecked")
    public NeuralNetwork(int inputNumber, int hiddenLayerCount, int hiddenPerLayer, int outputNumber, int weight,
            int learningRate) {

        inputSize = inputNumber;
        currentInput = 0;
        this.hiddenLayerSize = hiddenLayerCount;
        neuronPerHidden = hiddenPerLayer;
        outputSize = outputNumber;

        inputs = new LinkedList<>();

        hiddenLayers = new LinkedList[hiddenLayerCount];
        for (int i = 0; i < hiddenLayerCount; i++)
            hiddenLayers[i] = new LinkedList<>();

        outputs = new LinkedList<>();

        for (int i = 0; i < outputSize; i++) {
            outputs.add(new Neuron(hiddenPerLayer, false, weight, 0, learningRate));
        }

        for (int i = 0; i < inputNumber; i++) {
            inputs.add(new Neuron(1, true, weight, 0, 0));
        }

        for (int i = 0; i < hiddenLayerCount; i++) {
            for (int j = 0; j < neuronPerHidden; j++) {
                boolean nearInputs = i == 0;
                int inputForHidden = nearInputs ? inputNumber : hiddenPerLayer;
                hiddenLayers[i].add(new Neuron(inputForHidden, false, 0, learningRate));
            }
        }

        connect();

    }

    public boolean addInput(float newInput) {
        if (currentInput < inputSize) {
            inputs.get(currentInput).addInput(newInput);
            currentInput++;
            return true;
        }
        System.out.println("Error: input is full.");
        return false;
    }

    public boolean changeInput(int pos, float newInput) {
        inputs.get(pos).changeInput(pos, newInput);
        return false;
    }

    public float[] getOutput() {
        float[] results = new float[outputSize]; // Creates an array size of output size

        for (int i = 0; i < results.length; i++)
            results[i] = outputs.get(i).getOutput(); // Get results first
        // Then an error percentage.

        return results;
    }

    /**
     * Connects all neurons
     */
    private void connect() {
        if (hiddenLayers == null) {
            for (int i = 0; i < outputSize; i++) {
                Perceptron currentP_Output = outputs.get(i);
                for (int j = 0; j < inputSize; j++) {
                    Perceptron currentP_Input = inputs.get(j);
                    currentP_Output.addInput(currentP_Input);
                }
            }
        } else {
            LinkedList<Perceptron> currentLayer = hiddenLayers[hiddenLayerSize - 1];
            for (int i = 0; i < outputSize; i++) {
                Perceptron currentP_Output = outputs.get(i);
                for (int j = 0; j < currentLayer.size(); j++) {
                    Perceptron currentP_Hidden = currentLayer.get(j);
                    currentP_Output.addInput(currentP_Hidden);
                }
            }

            for (int i = hiddenLayerSize - 2; i >= 0; i--) {
                currentLayer = hiddenLayers[i];

                for (int j = 0; j < currentLayer.size(); j++) {
                    Perceptron currentP = currentLayer.get(j);
                    LinkedList<Perceptron> linkedHiddenLayer = hiddenLayers[i + 1];
                    for (int k = 0; k < linkedHiddenLayer.size(); k++) {
                        linkedHiddenLayer.get(k).addInput(currentP);
                    }
                }
            }

            currentLayer = hiddenLayers[0];
            for (int i = 0; i < currentLayer.size(); i++) {
                Perceptron currentP_Hidden = currentLayer.get(i);
                for (int j = 0; j < inputSize; j++) {
                    Perceptron currentP_Input = inputs.get(j);
                    currentP_Hidden.addInput(currentP_Input);
                }
            }

        }
    }

    /**
     * Do front propagation to get result and then do back propagation to correct
     * weights used in the network.
     * 
     * @param maxEpoch  Maximum number of rounds to be allowed during training
     * @param targetAcc The second condition to halt training process if the network
     *                  reaches a desired accuracy
     * @param expected  An array for use in training
     */
    public void train(int maxEpoch, float targetAcc, float[] expected) {
        if (expected.length != outputSize) {
            System.out.println("Error: Expected output array should have the same size as output count");
            return;
        }

        Random rng = new Random();

        for (int currentEpoch = 1; currentEpoch <= maxEpoch; currentEpoch++) {
            for (int i = 0; i < inputSize; i++) {
                inputs.get(i).changeInput(i, rng.nextInt(10));
            }

            float[] predicted = getOutput();
            float[] error = new float[outputSize];

            for (int i = 0; i < outputSize; i++) {
                error[i] = predicted[i] - expected[i];
            }

            // back propagates here

            // float[] fstGradiants = new float[outputSize];

            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < neuronPerHidden; j++) {
                    Perceptron n = outputs.get(i);
                    float derivFnValue = n.useDerivFn(predicted[i]);
                    float oldWeight = n.weights().get(j);
                    float learningRate = n.lr();
                    outputs.get(i).updateWeight(j, oldWeight + learningRate * error[i] * derivFnValue);
                }
            }

            float[] localGrads = new float[neuronPerHidden];

            for (int i = hiddenLayerSize; i >= 0; i--) {
                //edge number j
                for (int j = 0; j < neuronPerHidden; j++) {

                    
                    

                    if (i == hiddenLayerSize){

                    }else{
                        for (int k = 0; k < neuronPerHidden; k++) {
                        
                            hiddenLayers[i].get(j).updateWeight(k, );
                        }
                    }
                    
                }
            }

            // changes weight
        }

    }

}