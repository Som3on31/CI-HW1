import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

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
                         double learningRate) {
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
    public NeuralNetwork(int inputNumber, int hiddenLayerCount, int hiddenPerLayer, int outputNumber, double weight,
                         double learningRate) {

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

        System.out.println("Construction complete");
    }

    public boolean addInput(double newInput) {
        if (currentInput < inputSize) {
            inputs.get(currentInput).addInput(newInput);
            currentInput++;
            return true;
        }
        System.out.println("Error: input is full.");
        return false;
    }

    public boolean changeInput(int pos, double newInput) {
        inputs.get(pos).changeInput(0, newInput);
        return false;
    }

    public double[] getOutput() {
        double[] results = new double[outputSize]; // Creates an array size of output size

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
     * @param trainingSet A 2D array for use in training
     * @param expected  An array for use in training
     */
    public void train(int maxEpoch, double[][] trainingSet, double[] expected) {
        if (trainingSet.length != expected.length) {
            System.out.println("Error: Expected output array should have the same size as output count");
            return;
        }

        for (int currentEpoch = 0; currentEpoch < maxEpoch; currentEpoch++) {
            for (int i = 0; i < trainingSet[currentEpoch % 314].length; i++) {
                inputs.get(i).changeInput(0, trainingSet[currentEpoch % 314][i]);
            }

            double[] predicted = getOutput();
            double[] error = new double[outputSize];

            for (int i = 0; i < outputSize; i++) {
                error[i] = predicted[i] - expected[currentEpoch % 314];
            }

            // report error of each output
            System.out.print("epoch:" + currentEpoch + " ");
            for (int i = 0; i < error.length; i++) {
                System.out.print("error " + i + " :" + error[i] + " ");
                if (i == error.length - 1)
                    System.out.println();
            }

            // backpropagates here
            // get gradient of each neuron first (except input neurons lol)
            double[][] localGrads = new double[hiddenLayerSize + 1][neuronPerHidden]; // for hidden + output

            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < neuronPerHidden; j++) {
                    Perceptron n = outputs.get(i);
                    double derivFnValue = n.useDerivFn(n.getOutputRaw());

                    localGrads[hiddenLayerSize][j] = error[i] * derivFnValue;
                }
            }

            for (int i = hiddenLayerSize - 1; i >= 0; i--) {
                for (int j = 0; j < neuronPerHidden; j++) {
                    Perceptron currentP = hiddenLayers[i].get(j);
                    double derivOfSelf = currentP.useDerivFn(currentP.getOutputRaw());
                    double sumOfGradAndWeight = 0;
                    if (i == hiddenLayerSize - 1) {

                        for (int k = 0; k < outputs.size(); k++) {
                            LinkedList<Double> outputWeights = outputs.get(k).weights();
                            for (int k1 = 0; k1 < outputWeights.size(); k1++) {
                                sumOfGradAndWeight += localGrads[i + 1][k1] * outputWeights.get(k1);
                            }
                        }

                    } else {
                        for (int k = 0; k < neuronPerHidden; k++) {
                            LinkedList<Double> outputWeights = hiddenLayers[i + 1].get(k).weights();
                            for (int k1 = 0; k1 < outputWeights.size(); k1++) {
                                sumOfGradAndWeight += localGrads[i + 1][k1] * outputWeights.get(k1);
                            }
                        }
                    }
                    localGrads[i][j] = derivOfSelf * sumOfGradAndWeight;
                }
            }

            //update weights here
            //for outputs
            for (int i = 0; i < outputs.size(); i++) {
                Perceptron currentP = outputs.get(i);
                LinkedList<Double> outputWeights = currentP.weights();

                for (int j = 0; j < neuronPerHidden; j++) {
                    currentP.updateWeight(j, outputWeights.get(i) + currentP.lr() * localGrads[hiddenLayerSize][j] * currentP.getOutput());
                }
            }
            //for hidden
            for (int i = hiddenLayerSize - 1; i >= 0; i--) {

                int weightsToChange = i == 0 ? inputSize : neuronPerHidden;

                for (int j = 0; j < neuronPerHidden; j++) {
                    Perceptron currentH = hiddenLayers[i].get(j);
                    LinkedList<Double> hiddenWeights = currentH.weights();

                    for (int k = 0; k < weightsToChange; k++) {
                        currentH.updateWeight(k, hiddenWeights.get(k) + currentH.lr() * currentH.getOutput() * localGrads[i][j]);
                    }
                }
            }

        }

    }

}