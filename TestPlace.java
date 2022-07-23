import neurons.*;
import java.util.LinkedList;
import java.util.Random;

public class TestPlace {

    private final static String helloMSG = "Hello!";

    public static void main(String[] args) {
        System.out.println(helloMSG);
        InputNeuron neuron_prototype = new InputNeuron(4, true, 0, 0.1f);
        neuron_prototype.addInput(1);
        neuron_prototype.addInput(2);
        neuron_prototype.addInput(9);
        neuron_prototype.addInput(7);
        neuron_prototype.addInput(99999);
        neuron_prototype.addInput(433);
        neuron_prototype.addInput(777);

        System.out.println(neuron_prototype.getOutput());
        System.out.println("Full? : " + neuron_prototype.isFull());
        System.out.println("Empty? :" + neuron_prototype.isEmpty());
        System.out.println("\n------Test 2: when all are connected-------");

        Random rng = new Random();
        LinkedList<Perceptron> inputs = new LinkedList<>();
        int inputCount = 5;
        
        for (int i = 0; i < 5; i++) {
            Perceptron p = new InputNeuron(1, true, 0, 0.1f);
            p.addInput(rng.nextInt(10));
            inputs.add(p);
        }

        Perceptron output = new InputNeuron(5, false, 0, 0.1f);

        for(int i=0;i<inputs.size();i++){
            output.addInput(inputs.get(i));
        }

        System.out.println(output.getOutput());
    }
}