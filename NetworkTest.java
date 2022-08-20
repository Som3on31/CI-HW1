import java.io.File;
import java.util.Random;
import java.util.Scanner;


public class NetworkTest {
    public static void main(String[] args) throws Exception {
        int inputNum = 8;
        int hiddenPerLayer = 6;
        int hiddenLayerCount = 1;
        int outputNumber = 1;
        double lr = 0.05;
        double mr = 0.1;

        NeuralNetwork nn_test = new NeuralNetwork(inputNum, hiddenLayerCount, hiddenPerLayer, outputNumber, lr, mr);
        for (int i = 0; i < inputNum; i++) nn_test.addInput(0);

        //all vars to control generalization and samples to be used for accuracy testing
        double scale = 0.001;
        int trainSampleCount = (int) (314 * 0.9);


        try {
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

            //do not use numbers like 0.1 or it goes boom
            shuffleData(dataInputs, expected);
            generalize(dataInputs, expected, 1 / 80.0);

            //test #0
            nn_test.train(500, dataInputs, expected);



//            double[][] trainInputs = new double[trainSampleCount][dataInputs[0].length];
//            double[][] testInputs = new double[dataInputs.length - trainSampleCount][dataInputs[0].length];
//            double[] testExpected = new double[dataInputs.length - trainSampleCount];
//
//            System.arraycopy(dataInputs, 0, trainInputs, 0, trainSampleCount);
//            System.arraycopy(dataInputs, trainSampleCount, testInputs, 0, dataInputs.length - trainSampleCount);
//            System.arraycopy(expected, trainSampleCount, testExpected, 0, dataInputs.length - trainSampleCount);

            //for print debugging only
//            for(int i=0;i< dataInputs.length;i++){
//                for (int j=0;j< dataInputs[i].length;j++) System.out.print(dataInputs[i][j] + " ");
//                for (int j=0;j< expected[i].length;j++) System.out.print(expected[i][j] + " ");
//                System.out.println();
//            }

//            int[][] nnStructures_1 = new int[7][5];
//            int[][] nnStructures_2 = new int[7][5];


            s.close();
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    private static NeuralNetwork runTest1(double[] nnStructures, double[][] dataInputs, double[] expected, int k, int epoch) {
        int di_length = dataInputs.length;
        int expected_length = expected.length;
        if (di_length != expected_length) {
            System.out.println("Error: data input array must have the same size as expected array");
            return null;
        }

        double[] acc = new double[expected.length];
        double[] sse = new double[expected.length];

        int trainSize = (int) (dataInputs.length * 0.9);

        double[][] trainInputs = new double[trainSize][dataInputs[0].length];
        double[][] trainExpected = new double[trainSize][1];

        //construct and train networks here
        NeuralNetwork[] nns = new NeuralNetwork[k];
        for (int i = 0; i < nns.length; i++) {
            nns[i] = new NeuralNetwork(
                    (int) nnStructures[0], (int) nnStructures[1], (int) nnStructures[2],
                    (int) nnStructures[3], nnStructures[4], nnStructures[5]);
            int startValidation = 0;
            int endValidation = (int) (dataInputs.length * 0.1);

            //load all data into these training arrays then train it
            System.arraycopy(dataInputs,0,trainInputs,0,startValidation);
            System.arraycopy(expected,0,trainExpected,0,startValidation);
            System.arraycopy(dataInputs,endValidation,trainInputs,0,trainSize-endValidation);
            System.arraycopy(expected,endValidation,trainExpected,0,trainSize-endValidation);

            nns[i].train(epoch,trainInputs,trainExpected);

            //once it's trained, validate it to get a result

        }


        //return one that has the best validation result
        return null;
    }

    //WIP
    //to do this,  we must classify what 1 0 or 0 1 to look like
    //then, each run, we will get the result from our networks and classify where it belongs
    //for example, let 1 0 be class 1 and 0 1 be class 0. get result from the network and then
    //check if what we expect to get 1 0 is exactly from the network. classify the data before checking
    //it is true
    private static double[] runTest2(NeuralNetwork nn, double[][] dataInputs, double[] expected) {
        double[] result = new double[expected.length];


        for (int i = 0; i < expected.length; i++) {

        }
        return result;
    }

    private static void generalize(double[][] inputs, double[][] expected, double scale) {
        if (inputs.length != expected.length) {
            System.out.println("Error: both arrays must be the same size.");
            return;
        }

        for (int i = 0; i < inputs.length; i++) {
            double lowestVal = expected[i][0];
            for (int j=1;j<expected[0].length;j++) if (lowestVal > expected[i][j]) lowestVal = expected[i][j];
            for (int j = 0; j < inputs[0].length; j++) if (lowestVal > inputs[i][j]) lowestVal = inputs[i][j];

            for(int j=0;j<expected[0].length;j++){
                expected[i][j] -= lowestVal;
                expected[i][j] *= scale;
            }

            for (int j = 0; j < inputs[0].length; j++) {
                inputs[i][j] -= lowestVal;
                inputs[i][j] *= scale;
            }
        }
    }

    /**
     * @param inputs array of inputs data to be shuffled
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
     * @param nn neural netwrok to be tested
     * @param testInputs inputs to be used for testing
     * @param testExpected expected outputs to be used for testing
     * @return accuracy value in percent
     */
    private static double testAccuracy(NeuralNetwork nn, double[][] testInputs, double[] testExpected) {
        int correctPrediction = 0;

        for (int i = 0; i < testInputs.length; i++) {
            for (int j = 0; j < testInputs[i].length; j++) nn.changeInput(j, testInputs[i][j]);
            if (nn.getOutput()[0] == testExpected[i]) correctPrediction++;
        }

        return correctPrediction / (double) testInputs.length * 100;
    }
}