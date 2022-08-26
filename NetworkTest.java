import utilities.Pair;

import java.io.File;
import java.util.Random;
import java.util.Scanner;


public class NetworkTest {
    public static void main(String[] args) throws Exception {
        int inputNum = 8;
        int hiddenPerLayer = 6;
        int hiddenLayerCount = 1;
        int outputNumber = 1;
        double lr = 0.2;
        double mr = 0.15;

        //all vars to control generalization and samples to be used for accuracy testing
//        double scale = 0.001;
        double scale = 1 / 400.0;
//        int trainSampleCount = (int) (314 * 0.9);


        try {
            //load all test data into arrays initialized below
            File f = new File("./sample data.txt");
            Scanner s = new Scanner(f);

            int intCount = 0;
            double[][] dataInputs = new double[314][8];
            double[][] expected = new double[314][1];

            while (s.hasNext()) {
                int row = intCount / 9;
                int col = intCount % 9;
                if (col < 8) {
                    dataInputs[row][col] = s.nextInt();
                } else {
                    expected[row][0] = s.nextInt();
                }
                intCount++;
            }

            //do not use numbers like 0.1 ,or it goes boom
            shuffleData(dataInputs, expected);
            generalize(dataInputs, expected, scale);

            //test #0
            NeuralNetwork nn_test = new NeuralNetwork(inputNum, hiddenLayerCount, hiddenPerLayer, outputNumber,1.5,lr, mr);
            for (int i = 0; i < inputNum; i++) nn_test.addInput(0);

            nn_test.train(800, dataInputs, expected,scale);


            double[][] nn_data1 = new double[7][5];                     //pattern {inputs,hiddenNeurons,outputs,learning,momentum}
//            double[][] nn_data2 = new double[7][5];                     //same as above
            f = new File("./test1.txt");
            s = new Scanner(f);

            int doubleCount = 0;
            while (s.hasNext()) {
                int col = doubleCount % 5;
                int row = doubleCount / 5;
                nn_data1[row][col] = s.nextDouble();

                doubleCount++;
            }

            //test #1
            Pair<NeuralNetwork, double[]>[] nn_1 = new Pair[nn_data1.length];
            for (int i = 0; i < nn_1.length; i++) {
                nn_1[i] = runTest1(nn_data1[i], dataInputs, expected, 10, 400, scale);

                System.out.println("Network " + i + ": " + nn_data1[i][0] + "-" + nn_data1[i][1] + "-" + nn_data1[i][2]
                        + " with learning rate of " + nn_data1[i][3] + " and momentum rate of " + nn_data1[i][4]);
                System.out.println("Final accuracy: " + nn_1[i].second()[1] + "%");
                System.out.println("Train accuracy before validation: " + nn_1[i].second()[0] + "%");
            }
//
//            //test 2
//            double[][] nn_data2 = new double[7][5];
//            f = new File("./test2.txt");
//            s = new Scanner(f);
//
//            Pair<NeuralNetwork, double[]>[] nn_2 = new Pair[nn_data2.length];
//            for (int i = 0; i < nn_2.length; i++) {
//                nn_2[i] = runTest2(nn_data2[i], dataInputs, expected, 10, 400, 1);
//
//                System.out.println("Network " + i + ": " + nn_data2[0] + "-" + nn_data2[1] + "-" + nn_data2[2]
//                        + " with learning rate of " + nn_data2[3] + " and momentum rate of " + nn_data2[4]);
//                System.out.println("Final accuracy: " + nn_2[i].second()[1] + "%");
//                System.out.println("Train accuracy before validation: " + nn_1[i].second()[0] + "%");
//            }


            s.close();
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    private static Pair<NeuralNetwork, double[]> runTest1(double[] nnStructures, double[][] dataInputs, double[][] expected, int k, int epoch, double scale) {
        int di_length = dataInputs.length;
        int expected_length = expected.length;
        if (di_length != expected_length) {
            System.out.println("Error: data input array must have the same size as expected array");
            return null;
        }

        int vadSize = dataInputs.length / k;
        int trainSize = dataInputs.length - vadSize;

        double[][] trainInputs = new double[trainSize][dataInputs[0].length];
        double[][] trainExpected = new double[trainSize][expected[0].length];
        double[][] vadInputs = new double[vadSize][dataInputs[0].length];
        double[][] vadExpected = new double[vadSize][expected[0].length];

        //construct and train networks here
        NeuralNetwork[] nns = new NeuralNetwork[k];
        int startValidation = 0;
        int posShift = (dataInputs.length / k);
        int endValidation = posShift;
        Pair<NeuralNetwork, double[]> result = null;
        for (int i = 0; i < k; i++) {
            nns[i] = new NeuralNetwork(
                    (int) nnStructures[0], 1,(int) nnStructures[1], (int) nnStructures[2],
                    (int) nnStructures[3], nnStructures[4]);
            for (int j = 0; j < dataInputs[0].length; j++) nns[i].addInput(0);

            //load all data into these training arrays then train it
            System.arraycopy(dataInputs, 0, trainInputs, 0, startValidation);                               //0 to start
            System.arraycopy(expected, 0, trainExpected, 0, startValidation);
            System.arraycopy(dataInputs, endValidation, trainInputs, startValidation, dataInputs.length - endValidation);//end to end of array
            System.arraycopy(expected, endValidation, trainExpected, startValidation, dataInputs.length - endValidation);
            System.arraycopy(dataInputs, startValidation, vadInputs, 0, vadSize);                                   //validation side
            System.arraycopy(expected, startValidation, vadExpected, 0, vadSize);

            double trainAcc = nns[i].train(epoch, trainInputs, trainExpected,scale);

            //once it's trained, validate it to get a result
            double vadAcc = validateAccuracy(nns[i], vadInputs, vadExpected, scale);
            if (result == null || trainAcc < vadAcc && result.second()[1] < vadAcc) {         //need to fix null pointer
                double[] accOfTrainAndVad = {trainAcc, vadAcc};
                result = new Pair<>(nns[i], accOfTrainAndVad);
            }

            //update array pos to be validated
            startValidation += posShift;
            endValidation += posShift;
        }


        //return one that has the best validation result
        return result;
    }

    //WIP
    //to do this,  we must classify what 1 0 or 0 1 to look like
    //then, each run, we will get the result from our networks and classify where it belongs
    //for example, let 1 0 be class 1 and 0 1 be class 0. get result from the network and then
    //check if what we expect to get 1 0 is exactly from the network. classify the data before checking
    //it is true
    private static Pair<NeuralNetwork, double[]> runTest2(double[] nnStructures, double[][] dataInputs, double[][] expected, int k, int epoch, double scale) {
        int di_length = dataInputs.length;
        int expected_length = expected.length;
        if (di_length != expected_length) {
            System.out.println("Error: data input array must have the same size as expected array");
            return null;
        }

        int vadSize = dataInputs.length / k;
        int trainSize = dataInputs.length - vadSize;

        double[][] trainInputs = new double[trainSize][dataInputs[0].length];
        double[][] trainExpected = new double[trainSize][expected[0].length];
        double[][] vadInputs = new double[vadSize][dataInputs[0].length];
        double[][] vadExpected = new double[vadSize][expected[0].length];

        double[][] confusionMatrix = new double[2][2];

        //construct and train networks here
        NeuralNetwork[] nns = new NeuralNetwork[k];
        int startValidation = 0;
        int posShift = (dataInputs.length / k);
        int endValidation = posShift;

        for (int i = 0; i < k; i++) {
            nns[i] = new NeuralNetwork(
                    (int) nnStructures[0], (int) nnStructures[1], (int) nnStructures[2],
                    (int) nnStructures[3], nnStructures[4], nnStructures[5]);

            //load all data into these training arrays then train it
            System.arraycopy(dataInputs, 0, trainInputs, 0, startValidation);                               //0 to start
            System.arraycopy(expected, 0, trainExpected, 0, startValidation);
            System.arraycopy(dataInputs, endValidation, trainInputs, startValidation, dataInputs[0].length - endValidation);//end to end of array
            System.arraycopy(expected, endValidation, trainExpected, startValidation, dataInputs[0].length - endValidation);
            System.arraycopy(dataInputs, startValidation, vadInputs, 0, vadSize);                                   //validation side
            System.arraycopy(expected, startValidation, vadExpected, 0, vadSize);


            //update array pos to be validated
            startValidation += posShift;
            endValidation += posShift;

            nns[i].train(epoch, trainInputs, trainExpected,scale);
            for (int i1 = 0; i1 < vadInputs.length; i1++) {
                for (int j = 0; j < vadSize; j++) nns[i].changeInput(j, vadInputs[i1][j]);
                for (int j = 0; j < vadSize; j++) {

                }
            }
        }


        return null;
    }

    private static void generalize(double[][] inputs, double[][] expected, double scale) {
        if (inputs.length != expected.length) {
            System.out.println("Error: both arrays must be the same size.");
            return;
        }

        for (int i = 0; i < inputs.length; i++) {
            double lowestVal = expected[i][0];
            for (int j = 1; j < expected[0].length; j++) if (lowestVal > expected[i][j]) lowestVal = expected[i][j];
            for (int j = 0; j < inputs[0].length; j++) if (lowestVal > inputs[i][j]) lowestVal = inputs[i][j];

            for (int j = 0; j < expected[0].length; j++) {
//                expected[i][j] -= lowestVal;
                expected[i][j] *= scale;
            }

            for (int j = 0; j < inputs[0].length; j++) {
//                inputs[i][j] -= lowestVal;
                inputs[i][j] *= scale;
            }
        }
    }

    /**
     * Shuffle all data for testing.
     *
     * @param inputs   array of inputs data to be shuffled
     * @param expected array of expected data to be shuffled
     */
    private static void shuffleData(double[][] inputs, double[][] expected) {
        Random rng = new Random();
        int range = inputs.length;

        for (int i = 0; i < inputs.length; i++) {
            int pos = rng.nextInt(range);
            double[] selectedInp = inputs[pos];
            double[] selectedEx = expected[pos];
            inputs[pos] = inputs[i];
            expected[pos] = expected[i];
            inputs[i] = selectedInp;
            expected[i] = selectedEx;
        }
    }

    /**
     * @param nn           neural network to be tested
     * @param testInputs   inputs to be used for testing
     * @param testExpected expected outputs to be used for testing
     * @return accuracy value in percent
     */
    private static double validateAccuracy(NeuralNetwork nn, double[][] testInputs, double[][] testExpected, double scale) {
        int correctPrediction = 0;

        for (int i = 0; i < testInputs.length; i++) {
            for (int j = 0; j < testInputs[i].length; j++) nn.changeInput(j, testInputs[i][j]);
            for (int j = 0; j < testExpected[i].length; j++) {
                int predicted = (int) (nn.getOutput()[j] * scale);
                int expected = (int) (testExpected[i][j] * scale);

                boolean valueAccepted = expected - predicted == 0;
                if (valueAccepted) correctPrediction++;
            }
        }

        return correctPrediction / (double) testInputs.length * 100;
    }
}