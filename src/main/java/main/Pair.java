package main;

/**
 * Class to create a pair of objects of whatever types.
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1,T2> {
    private T1 object1;
    private T2 object2;

    /**
     * Constructor for a pair of objects
     * @param object1  First object
     * @param object2  Second object
     */
    public Pair(T1 object1, T2 object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    public T1 getObject1(){
        return object1;
    }

    public T2 getObject2(){
        return object2;
    }
}
