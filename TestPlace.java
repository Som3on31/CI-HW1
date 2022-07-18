public class TestPlace{

    private final static String helloMSG = "Hello!";

    public static void main(String[] args){
        System.out.println(helloMSG);
        Neuron neuron_prototype = new Neuron(4, true, 0);
        neuron_prototype.addInput(1);
        neuron_prototype.addInput(2);
        neuron_prototype.addInput(9);
        neuron_prototype.addInput(7);
        neuron_prototype.addInput(99999);

        boolean full = neuron_prototype.isFull();
        System.out.println(neuron_prototype.getOutput());
        System.out.println("Full? : " + full);
        System.out.println("\n------Test 2: when all are connected-------");
        
    }
}