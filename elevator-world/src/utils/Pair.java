package utils;

public  class Pair<K, V> {
    private  K fst;
    private  V snd;
    
    public Pair (K fst, V snd) {
       this.fst = fst;
       this.snd = snd; 
    }

    public K getFst() {
        return fst;
    }

    public V getSnd() {
        return snd;
    }


}