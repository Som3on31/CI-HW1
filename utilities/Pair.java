package utilities;

public class Pair <F,S>{
    
    private F first;
    private S second;

    public Pair(F fst,S snd){
        first = fst;
        second = snd;
    }

    public F first() {
        return first;
    }

    public S second(){
        return second;
    }
}
