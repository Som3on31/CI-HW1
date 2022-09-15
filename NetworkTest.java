import utilities.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class NetworkTest {

    private static final double[] case01 = {0, 1};
    private static final double[] case10 = {1, 0};
    private static int confusionCount = 0;

    public static void main(String[] args) throws Exception {

        double scale = 1 / 100.0;


        try {
            //load all test data into arrays initialized below
            File f = new File("./sample data.txt");
            Scanner s = new Scanner(f);

            int dataCount = 0;
            double[][] dataInputs = new double[314][8];
            double[][] expected = new double[314][1];

            while (s.hasNext()) {
                int row = dataCount / 9;
                int col = dataCount % 9;
                if (col < 8) {
                    dataInputs[row][col] = s.nextInt();
                } else {
                    expected[row][0] = s.nextInt();
                }
                dataCount++;
            }

            //do not use numbers like 0.1 ,or it goes boom
            shuffleData(dataInputs, expected);
            double[] deductedValues = generalize(dataInputs, expected, scale);

            double[][] nn_data1 = new double[7][5];                     //pattern {inputs,hiddenNeurons,outputs,learning,momentum}
            String test1File = "./test1v2.txt";
            loadData(nn_data1, test1File);

            //test #1
            System.out.println("---------------------Test 1: Water level---------------------");
            Pair<NeuralNetwork, double[]>[] nn_1 = new Pair[nn_data1.length];
            for (int i = 0; i < nn_1.length; i++) {
                nn_1[i] = runTest1(nn_data1[i], dataInputs, expected, scale);

                int selectedPos = (int) nn_1[i].second()[2] + 1;
                System.out.println("Network " + i + ": " + nn_data1[i][0] + "-" + nn_data1[i][1] + "-" + nn_data1[i][2]
                        + " with learning rate of " + nn_data1[i][3] + " and momentum rate of " + nn_data1[i][4]
                        + " from " + selectedPos + "/" + 10);
                assert nn_1[i] != null;
                System.out.println("Final accuracy: " + Math.round(nn_1[i].second()[1] * 100) / (double) 100 + "%");
                System.out.println("Train accuracy before validation: " + Math.round(nn_1[i].second()[0] * 100) / (double) 100 + "%");
                System.out.println("-------------------------------------------------");
            }

//            //test 2
            double[][] nn_data2 = new double[7][5];
            String test2Data = "./test2.txt";
            loadData(nn_data2, test2Data);

            dataInputs = new double[200][2];
            expected = new double[200][2];
            f = new File("./cross.pat");
            s = new Scanner(f);

            dataCount = 0;

            while (s.hasNext()) {
                String current = s.next();
                if (current.contains("p")) current = s.next();
                int row = dataCount / 2;
                int col = dataCount % 2;
                boolean isInput = dataCount % 4 < 2;
                if (isInput) {
                    dataInputs[row/2][col] = Double.parseDouble(current);
                } else {
                    expected[row/2][col] = Double.parseDouble(current);
                }

                dataCount++;
            }

            System.out.println("\n\n---------------------Test 2: Confusion Matrix---------------------");
            Pair<NeuralNetwork, double[]>[] nn_2 = new Pair[nn_data2.length];
            for (int i = 0; i < nn_2.length; i++) {
                nn_2[i] = runTest2(nn_data2[i], dataInputs, expected);

                int selectedPos = (int) nn_2[i].second()[2] + 1;
                assert nn_2[i] != null;
                System.out.println("Network " + i + ": " + nn_data1[i][0] + "-" + nn_data1[i][1] + "-" + nn_data1[i][2]
                        + " with learning rate of " + nn_data1[i][3] + " and momentum rate of " + nn_data1[i][4]
                        + " from " + selectedPos + "/" + 10);
                assert nn_2[i] != null;
                System.out.println("Final accuracy: " + nn_2[i].second()[1] + "%");
                System.out.println("Train accuracy before validation: " + nn_2[i].second()[0] + "%");
                System.out.println("-------------------------------------------------");
            }


            s.close();
        } catch (Exception e) {
            throw new Exception(e);
        }


    }

    private static Pair<NeuralNetwork, double[]> runTest1(double[] nnStructures, double[][] dataInputs, double[][] expected, double scale) {
        int di_length = dataInputs.length;
        int expected_length = expected.length;
        if (di_length != expected_length) {
            System.out.println("Error: data input array must have the same size as expected array");
            return null;
        }

        int vadSize = dataInputs.length / 10;
        int trainSize = dataInputs.length - vadSize;

        double[][] trainInputs = new double[trainSize][dataInputs[0].length];
        double[][] trainExpected = new double[trainSize][expected[0].length];
        double[][] vadInputs = new double[vadSize][dataInputs[0].length];
        double[][] vadExpected = new double[vadSize][expected[0].length];

        //construct and train networks here
        NeuralNetwork[] nns = new NeuralNetwork[10];
        int startValidation = 0;
        int posShift = (dataInputs.length / 10);
        int endValidation = posShift;

        double[] emptyAcc = {0, 0};
        Pair<NeuralNetwork, double[]> result = new Pair<>(null, emptyAcc);
        for (int i = 0; i < 10; i++) {
            nns[i] = new NeuralNetwork(
                    (int) nnStructures[0], 1, (int) nnStructures[1], (int) nnStructures[2],
                    (int) nnStructures[3], nnStructures[4]);
            for (int j = 0; j < dataInputs[0].length; j++) nns[i].addInput(0);

            //load all data into these training arrays then train it
            System.arraycopy(dataInputs, 0, trainInputs, 0, startValidation);                               //0 to start
            System.arraycopy(expected, 0, trainExpected, 0, startValidation);
            System.arraycopy(dataInputs, endValidation, trainInputs, startValidation, dataInputs.length - endValidation);//end to end of array
            System.arraycopy(expected, endValidation, trainExpected, startValidation, dataInputs.length - endValidation);
            System.arraycopy(dataInputs, startValidation, vadInputs, 0, vadSize);                                   //validation side
            System.arraycopy(expected, startValidation, vadExpected, 0, vadSize);

            double trainAcc = nns[i].train(800, trainInputs, trainExpected, scale, false);

            //once it's trained, validate it to get a result
            double vadAcc = validateAccuracy(nns[i], vadInputs, vadExpected, scale);
            System.out.println("Validated acc: " + Math.round(vadAcc * 100) / 100.0 + " Train acc: " + Math.round(trainAcc * 100) / 100.0);
            if (result.first() == null || trainAcc < vadAcc && result.second()[1] < vadAcc) {         //need to fix null pointer
                double[] accOfTrainAndVad = {trainAcc, vadAcc, i};
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
    private static Pair<NeuralNetwork, double[]> runTest2(double[] nnStructures, double[][] dataInputs, double[][] expected) {
        int di_length = dataInputs.length;
        int expected_length = expected.length;
        if (di_length != expected_length) {
            System.out.println("Error: data input array must have the same size as expected array");
            return null;
        }

        int vadSize = dataInputs.length / 10;
        int trainSize = dataInputs.length - vadSize;

        double[][] trainInputs = new double[trainSize][dataInputs[0].length];
        double[][] trainExpected = new double[trainSize][expected[0].length];
        double[][] vadInputs = new double[vadSize][dataInputs[0].length];
        double[][] vadExpected = new double[vadSize][expected[0].length];

        //construct and train networks here
        NeuralNetwork[] nns = new NeuralNetwork[10];
        int startValidation = 0;
        int posShift = (dataInputs.length / 10);
        int endValidation = posShift;

        final double[] emptyAcc = {0, 0};
        Pair<NeuralNetwork, double[]> result = new Pair<>(null, emptyAcc);

        for (int i = 0; i < 10; i++) {
            nns[i] = new NeuralNetwork(
                    (int) nnStructures[0], 1, (int) nnStructures[1], (int) nnStructures[2],
                    (int) nnStructures[3], nnStructures[4]);
            for (int j = 0; j < dataInputs[0].length; j++) nns[i].addInput(0);

            //load all data into these training arrays then train it
            System.arraycopy(dataInputs, 0, trainInputs, 0, startValidation);                               //0 to start
            System.arraycopy(expected, 0, trainExpected, 0, startValidation);
            System.arraycopy(dataInputs, endValidation, trainInputs, startValidation, dataInputs.length - endValidation);//end to end of array
            System.arraycopy(expected, endValidation, trainExpected, startValidation, dataInputs.length - endValidation);
            System.arraycopy(dataInputs, startValidation, vadInputs, 0, vadSize);                                   //validation side
            System.arraycopy(expected, startValidation, vadExpected, 0, vadSize);

            //update array pos to be validated
            startValidation += posShift;
            endValidation += posShift;

            double trainAcc = nns[i].train(500, trainInputs, trainExpected, 1, true);
            int[][] confusion = getConfusion(nns[i], vadInputs, vadExpected);

            try {
                StringBuilder sb = new StringBuilder();
                File f = new File("./neuronResult/confusions/confusion" + confusionCount);
                FileWriter fw = new FileWriter(f);
                f.createNewFile();

                for (int[] ints : confusion) {
                    for (int anInt : ints) {
                        sb.append(anInt);
                        sb.append(" ");
                    }
                    sb.append("\n");
                }

                fw.write(sb.toString());
                fw.close();
                confusionCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }

            double acc = (confusion[0][0] + confusion[1][1]) / (double) vadSize * 100;
            System.out.println("Validated acc: " + Math.round(acc * 100) / 100.0 + " Train acc: " + Math.round(trainAcc * 100) / 100.0);
            if (result.first() == null || trainAcc < acc && acc > result.second()[1]) {
                double[] accOfTrainAndVad = {trainAcc, acc, i};
                result = new Pair<>(nns[i], accOfTrainAndVad);
            }
        }

        return result;
    }

    private static double[] generalize(double[][] inputs, double[][] expected, double scale) {
        if (inputs.length != expected.length) {
            System.out.println("Error: both arrays must be the same size.");
            return null;
        }

        double[] deductedValues = new double[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            double lowestVal = expected[i][0];
            for (int j = 1; j < expected[0].length; j++) if (lowestVal > expected[i][j]) lowestVal = expected[i][j];
            for (int j = 0; j < inputs[0].length; j++) if (lowestVal > inputs[i][j]) lowestVal = inputs[i][j];

            deductedValues[i] = lowestVal;

            for (int j = 0; j < expected[0].length; j++) {
                expected[i][j] -= lowestVal;
                expected[i][j] *= scale;
            }

            for (int j = 0; j < inputs[0].length; j++) {
                inputs[i][j] -= lowestVal;
                inputs[i][j] *= scale;
            }
        }

        return deductedValues;
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
                int predicted = (int) (nn.getOutput()[j] / scale);
                int expected = (int) (testExpected[i][j] / scale);

                boolean valueAccepted = expected - predicted == 0;
                if (valueAccepted) correctPrediction++;
            }
        }

        return correctPrediction / (double) testInputs.length * 100;
    }

    private static int[][] getConfusion(NeuralNetwork nn, double[][] inputs, double[][] expected) {
        int[][] confusionMatrix = new int[2][2];
        for (int[] matrix : confusionMatrix) Arrays.fill(matrix, 0);

        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputs[i].length; j++) nn.changeInput(j, inputs[i][j]);

            double[] outputs = nn.getOutput();
            if (Arrays.equals(expected[i], case10)) {
                if (outputs[0] > outputs[1]) confusionMatrix[0][0]++;
                else confusionMatrix[0][1]++;
            } else if (Arrays.equals(expected[i], case01)) {
                if (outputs[0] > outputs[1]) confusionMatrix[1][0]++;
                else confusionMatrix[1][1]++;
            }
        }

        return confusionMatrix;
    }

    private static void loadData(double[][] nn_data, String filePath) {
        File f = new File(filePath);

        try {
            Scanner s = new Scanner(f);
            int doubleCount = 0;

            while (s.hasNext()) {
                int col = doubleCount % 5;
                int row = doubleCount / 5;
                nn_data[row][col] = s.nextDouble();

                doubleCount++;
            }

            s.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}