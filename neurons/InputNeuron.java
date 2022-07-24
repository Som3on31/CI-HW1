package neurons;

public class InputNeuron extends Neuron{
    
    public InputNeuron(float input){
        super(1,true,0,0);
        super.addInput(input);
    }

    @Override
    public float getOutput(){
        return super.getOutput();
    }
}
