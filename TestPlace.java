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

        float output_prototype = neuron_prototype.getOutput();
        System.out.println(output_prototype);
        System.out.println(neuron_prototype.useActivationFn(output_prototype));
        System.out.println("Full? : " + neuron_prototype.isFull());
        System.out.println("Empty? :" + neuron_prototype.isEmpty());
        System.out.println("\n------Test 2: when all are connected-------");

        Random rng = new Random();
        LinkedList<Perceptron> inputs = new LinkedList<>();
        int inputCount = 5;
        
        for (int i = 0; i < 5; i++) {
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
    }
}