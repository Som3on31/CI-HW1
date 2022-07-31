public class NetworkTest {
    public static void main(String[] args){
        NeuralNetwork nn_test = new NeuralNetwork(4, 1, 6, 2,0.0004f);

        float[] expected_test = {150,160};
        for (int i=0;i<4;i++)nn_test.addInput(0);

        nn_test.train(30, 80, expected_test);


    }
}