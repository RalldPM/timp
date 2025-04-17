package main;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.Vector;

public class AntCollection {
    private final Vector<AbstractAnt> antVector = new Vector<>();
    private final HashSet<Integer> antSet = new HashSet<>();
    private final TreeMap<Integer, Integer> antMap = new TreeMap<>();
    private static volatile AntCollection instance;

    private AntCollection() {}

    public TreeMap<Integer, Integer> antMap() {return antMap;}

    public HashSet<Integer> antSet() {return antSet;}

    public Vector<AbstractAnt> antVector() {return antVector;}

    public static AntCollection ants() {
        AntCollection localInstance = instance;
        if (localInstance == null) {
            synchronized (AntCollection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AntCollection();
                }
            }
        }
        return localInstance;
    }
}

