import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.File;
import java.util.Scanner;


public class NetworkTest {
    public static void main(String[] args) throws Exception {
        int inputNum = 8;
        int hiddenPerLayer = 6;
        int hiddenLayerCount = 1;
        int outputNumber = 1;
        double lr = 0.0000000000000001;
        NeuralNetwork nn_test = new NeuralNetwork(inputNum, hiddenLayerCount, hiddenPerLayer, outputNumber,lr);
        for (int i=0;i<inputNum;i++)nn_test.addInput(0);

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
                    dataInputs[row][col] = s.nextInt();
                }else{
                    expected[row] = s.nextInt();
                }
                intCount++;
            }

            nn_test.train(500,dataInputs,expected);

        }catch(Exception e){
            throw new Exception(e);
        }

    }
}