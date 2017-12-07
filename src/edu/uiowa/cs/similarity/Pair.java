package edu.uiowa.cs.similarity;

import java.util.List;
import java.util.Map;

public class Pair {
    Map<String, List<String>> mapPair;
    Double doublePair;
    
    public Pair(Map<String, List<String>> mapPair, Double doublePair) {
        this.mapPair = mapPair;
        this.doublePair = doublePair;
    }
    
}
