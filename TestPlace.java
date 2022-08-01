import neurons.*;
import java.util.LinkedList;
import java.util.Random;

public class TestPlace {

    private final static String helloMSG = "Hello!";

    public static void main(String[] args) {
        System.out.println(helloMSG);
        Neuron neuron_prototype = new Neuron(4, true, 0,0);
        neuron_prototype.addInput(1);
        neuron_prototype.addInput(2);
        neuron_prototype.addInput(9);
        neuron_prototype.addInput(7);
        neuron_prototype.addInput(99999);
        neuron_prototype.addInput(433);
        neuron_prototype.addInput(777);

        double output_prototype = neuron_prototype.getOutput();
        System.out.println(output_prototype);
        System.out.println(neuron_prototype.useActivationFn(output_prototype));
        System.out.println("Full? : " + neuron_prototype.isFull());
        System.out.println("Empty? :" + neuron_prototype.isEmpty());
        System.out.println("\n------Test 2: when all are connected-------");

        Random rng = new Random();
        LinkedList<Perceptron> inputs = new LinkedList<>();
        int inputCount = 5;
        
        for (int i = 0; i < inputCount; i++) {
            Perceptron p = new Neuron(1, true, 0,0);
            p.addInput(rng.nextInt(10));
            inputs.add(p);
        }

        Perceptron output = new Neuron(5, false, 0,0);

        for(int i=0;i<inputs.size();i++){
            output.addInput(inputs.get(i));
        }

        System.out.println(output.getOutput());
        System.out.println(output.useActivationFn(output.getOutput()));

        System.out.println("\n------Test 3: constructing a network-------");

        int inputNum = 3;
        int hiddenLayerCount = 1;
        int hiddenPerLayer = 6;
        int outputNum = 2;
        NeuralNetwork nn_proto = new NeuralNetwork(inputNum,hiddenLayerCount, hiddenPerLayer, outputNum,0);

        // n0.addInput(1);
        // n1.addInput(5);
        // n2.addInput(2);

        nn_proto.addInput(1);
        nn_proto.addInput(5);
        nn_proto.addInput(2);
        nn_proto.addInput(999);
        nn_proto.addInput(55555555);

        double[] outputArray = nn_proto.getOutput();
        for (double result:outputArray){
            System.out.println(result + " ");
        }
        
        // float[] expectedOutput = {2,3};

        Perceptron test = new Neuron(1, true, 0, 0, 0);
        test.addInput(3);
        test.changeInput(0, 5);
        System.out.println("e");
    }
}