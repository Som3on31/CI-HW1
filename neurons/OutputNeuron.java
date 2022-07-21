package neurons;

public class OutputNeuron implements Perceptron{

    


    @Override
    public boolean addInput(Perceptron neuron) {
        
        return false;
    }

    @Override
    public float getOutput() {
        
        return 0;
    }

    @Override
    public boolean isEmpty() {
        
        return false;
    }

    @Override
    public boolean isFull() {
   
        return false;
    }

    @Override
    public float getError() {
        
        return 0;
    }
    
}
