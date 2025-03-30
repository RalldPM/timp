package main;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.Vector;

public class AntCollection {
    private final Vector<AbstractAnt> antVector = new Vector<>();
    private final HashSet<Integer> antSet = new HashSet<>();
    private final TreeMap<Integer, Integer> antMap = new TreeMap<>();

    public TreeMap<Integer, Integer> antMap() {return antMap;}

    public HashSet<Integer> antSet() {return antSet;}

    public Vector<AbstractAnt> antVector() {return antVector;}
}

