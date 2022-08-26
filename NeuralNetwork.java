import java.util.LinkedList;

import neurons.*;

public class NeuralNetwork {

    // Linked List to store neurons
    private final LinkedList<Perceptron> inputs;
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
                         double learningRate, double momentumRate) {
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
            outputs.add(new Neuron(hiddenPerLayer, false, 0, learningRate, momentumRate));
        }

        for (int i = 0; i < inputNumber; i++) {
            inputs.add(new Neuron(1, true, 1,0, 0, 0));
        }

        for (int i = 0; i < hiddenLayerCount; i++) {
            for (int j = 0; j < neuronPerHidden; j++) {
                boolean nearInputs = i == 0;
                int inputForHidden = nearInputs ? inputNumber : hiddenPerLayer;
                hiddenLayers[i].add(new Neuron(inputForHidden, false, 0, learningRate, momentumRate));
            }
        }

        connect();

        System.out.println("Construction complete");
    }

    @SuppressWarnings("unchecked")
    public NeuralNetwork(int inputNumber, int hiddenLayerCount, int hiddenPerLayer, int outputNumber, double weight,
                         double learningRate, double momentumRate) {

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
            outputs.add(new Neuron(hiddenPerLayer, false, weight, 0, learningRate, momentumRate));
        }

        for (int i = 0; i < inputNumber; i++) {
            inputs.add(new Neuron(1, true, 1, 0, 0, 0));
        }

        for (int i = 0; i < hiddenLayerCount; i++) {
            for (int j = 0; j < neuronPerHidden; j++) {
                boolean nearInputs = i == 0;
                int inputForHidden = nearInputs ? inputNumber : hiddenPerLayer;
                hiddenLayers[i].add(new Neuron(inputForHidden, false, 0, learningRate, momentumRate));
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
     * @param maxEpoch    Maximum number of rounds to be allowed during training
     * @param trainingSet A 2D array for use in training
     * @param expected    An array for use in training
     */
    public double train(int maxEpoch, double[][] trainingSet, double[][] expected, double scale) {
        if (trainingSet.length != expected.length) {
            System.out.println("Error: Expected output array should have the same size as output count");
            return 0;
        }

        //saves weights from previous epoch
        LinkedList<LinkedList<Double>>[] previousWeights = new LinkedList[hiddenLayerSize + 1];
        int correctlyPredicted = 0;
        for (int i = hiddenLayerSize; i >= 0; i--) {
            int maxRow = i == hiddenLayerSize ? outputSize : neuronPerHidden;
            previousWeights[i] = new LinkedList<>();

            for (int j = 0; j < maxRow; j++) {
                //creates an empty list to add to the outer list
                LinkedList<Double> emptyList = new LinkedList<>();
                previousWeights[i].add(emptyList);

                LinkedList<Double> weights = i == hiddenLayerSize ? outputs.get(j).weights() : hiddenLayers[i].get(j).weights();
                for (int k = 0; k < weights.size(); k++) {
                    previousWeights[i].get(j).add(weights.get(k));
                }
            }
        }

        for (int currentEpoch = 0; currentEpoch < maxEpoch; currentEpoch++) {
            for (int i = 0; i < trainingSet[currentEpoch % expected.length].length; i++) {
                inputs.get(i).changeInput(0, trainingSet[currentEpoch % expected.length][i]);
            }

            double[] predicted = getOutput();
            double[] error = new double[outputSize];

            for (int i = 0; i < outputSize; i++) {
                error[i] = expected[currentEpoch % expected.length][i] - predicted[i];
                boolean valueAccepted = Math.abs(expected[currentEpoch % expected.length][i] / scale) - (Math.round(Math.abs(predicted[i]/ scale))) == 0;
//                System.out.println("Value accepted? " + valueAccepted);
                if (valueAccepted){
                    correctlyPredicted++;

                }

            }

            // report error of each output
//            System.out.print("epoch:" + currentEpoch + " ");
            for (int i = 0; i < error.length; i++) {
//                System.out.print("error " + i + " :" + error[i] + " ");
                if (i == error.length - 1) {
                    double sse = 0;
                    for (double v : error) {
                        sse += Math.pow(v, 2);
                    }
                    double mse = sse / (double) outputSize;
//                    System.out.println("mse: " + mse);
//
//                    System.out.print("Predicted: ");
//                    for (double sample : predicted) System.out.print(Math.round(sample/scale) + " ");
//                    System.out.print("\nExpected: ");
//                    for (double sample : expected[currentEpoch % expected.length]) System.out.print(sample/scale + " ");
//                    System.out.print("\nDifference: ");
//                    for (int j = 0; j < expected[currentEpoch % expected.length].length; j++) {
//                        System.out.print(Math.round(Math.abs(error[j])/ scale) + " ");
//                    }
//                    System.out.println();


                }

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

            //hidden layer i
            for (int i = hiddenLayerSize - 1; i >= 0; i--) {
                //hidden neuron j layer i
                for (int j = 0; j < neuronPerHidden; j++) {
                    Perceptron currentP = hiddenLayers[i].get(j);
                    double derivOfSelf = currentP.useDerivFn(currentP.getOutputRaw());
                    double sumOfGradAndWeight = 0;
                    // if it is near output layer
                    if (i == hiddenLayerSize - 1) {

                        // for each output neuron connected to hidden j layer i
                        for (int k = 0; k < outputs.size(); k++) {
                            double outputWeightToJ = outputs.get(k).weights().get(j);
                            sumOfGradAndWeight += localGrads[i + 1][k] * outputWeightToJ;
                        }

                    } else {
                        for (int k = 0; k < neuronPerHidden; k++) {
                            double outputWeightToJ = hiddenLayers[i + 1].get(k).weights().get(j);
                            sumOfGradAndWeight += localGrads[i + 1][k] * outputWeightToJ;
                        }
                    }
                    localGrads[i][j] = derivOfSelf * sumOfGradAndWeight;
                }
            }

            //keeps all current weights for previousWeights
            LinkedList<LinkedList<Double>>[] savedWeights = new LinkedList[hiddenLayerSize + 1];
            if (currentEpoch > 0)
                for (int i = hiddenLayerSize; i >= 0; i--) {
                    int maxRow = i == hiddenLayerSize ? outputSize : neuronPerHidden;
                    savedWeights[i] = new LinkedList<>();

                    for (int j = 0; j < maxRow; j++) {
                        //creates an empty list to add to the outer list
                        LinkedList<Double> emptyList = new LinkedList<>();
                        savedWeights[i].add(emptyList);

                        LinkedList<Double> weights = i == hiddenLayerSize ? outputs.get(j).weights() : hiddenLayers[i].get(j).weights();
                        for (int k = 0; k < weights.size(); k++) {
                            savedWeights[i].get(j).add(weights.get(k));
                        }
                    }
                }

            //update weights here
            //for outputs
            for (int i = 0; i < outputs.size(); i++) {
                Perceptron currentP = outputs.get(i);
                LinkedList<Double> outputWeights = currentP.weights();
                for (int j = 0; j < neuronPerHidden; j++) {
                    currentP.updateWeight(j, outputWeights.get(j) + currentP.lr() * localGrads[hiddenLayerSize][j] * currentP.getOutput()
                            + currentP.mr() * (outputWeights.get(j) - previousWeights[hiddenLayerSize].get(i).get(j)));
                }
            }
            //for hidden
            for (int i = hiddenLayerSize - 1; i >= 0; i--) {
                //check if it's currently close to input layer
                int weightsToChange = i == 0 ? inputSize : neuronPerHidden;
                //then update each neuron
                for (int j = 0; j < neuronPerHidden; j++) {
                    Perceptron currentH = hiddenLayers[i].get(j);
                    LinkedList<Double> hiddenWeights = currentH.weights();

                    //finally, loop over all connected inputs
                    for (int k = 0; k < weightsToChange; k++) {
                        currentH.updateWeight(k, hiddenWeights.get(k) + currentH.lr() * currentH.getOutput() * localGrads[i][j]
                                + currentH.mr() * (hiddenWeights.get(k) - previousWeights[i].get(j).get(k)));
                    }
                }
            }

            //finally, update weights for epoch-1 array of linked lists if
            //it passes the first run of training process
            if (currentEpoch > 0) {
                for (int i = 0; i < previousWeights.length; i++) {
                    previousWeights[i] = savedWeights[i];
                }
            }
            int kek = 0;            //just a placeholder for breakpoint debugging
        }

        double acc = correctlyPredicted / (double) maxEpoch * 100;
        System.out.println("Train accuracy: " + acc);

        return acc;
    }

}