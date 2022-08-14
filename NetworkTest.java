import java.io.File;
import java.util.Scanner;


public class NetworkTest {
    public static void main(String[] args) throws Exception {
        int inputNum = 8;
        int hiddenPerLayer = 6;
        int hiddenLayerCount = 1;
        int outputNumber = 1;
        double lr = 0.01;
        double mr = 0.1;

        NeuralNetwork nn_test = new NeuralNetwork(inputNum, hiddenLayerCount, hiddenPerLayer, outputNumber,lr,mr);
        for (int i=0;i<inputNum;i++)nn_test.addInput(0);

        double scale = 0.001;


        try{
            File f = new File("./sample data.txt");
            Scanner s = new Scanner(f);

            int intCount = 0;
            double[][] dataInputs = new double[314][8];
            double[] expected = new double[314];

            while(s.hasNext()){
                int row = intCount / 9;
                int col = intCount % 9;
                if (col < 8){
                    dataInputs[row][col] = s.nextInt() * scale;
                }else{
                    expected[row] = s.nextInt() * scale;
                }
                intCount++;
            }

            nn_test.train(500,dataInputs,expected);


            s.close();
        }catch(Exception e){
            throw new Exception(e);
        }

    }

    private double[] testAcc(NeuralNetwork nn, double[][] dataInputs, double[] expected){
        int di_length = dataInputs.length;
        int expected_length = expected.length;
        if (di_length != expected_length) {
            System.out.println("Error: data input array must have the same size as expected array");
            return null;
        }

        double[] acc = new double[expected.length];

        for(int i=0;i<expected_length;i++) {
            for (int j=0;j<dataInputs[0].length;j++){
                nn.changeInput(j,dataInputs[i][j]);
            }
            double[] predicted = nn.getOutput();
            double err = Math.abs(predicted[0]-expected[i]);

            acc[i] = (expected[i]-err)/expected[i];
        }

        return acc;
    }

    public double[] getConfusion(NeuralNetwork nn, double[][] dataInputs, double[] expected){
        double[] result = new double[expected.length];

        for(int i=0;i< expected.length;i++){

        }

        return result;
    }
}