
package edu.uiowa.cs.similarity;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class MainTest {
    
    public MainTest() {
    }
    
    @Test
    public void testEuclideanSimilarity() {
        System.out.println("EuclideanSimilarity");
        String word1 = "man";
        String word2 = "liver";
        Map<String, Integer> word1Map = new HashMap<>();
        word1Map.put("cat", 5);
        word1Map.put("dog", 3);
        word1Map.put("John", 2);
        Map<String, Integer> word2Map = new HashMap<>();
        word2Map.put("cat", 2);
        word2Map.put("dog", 1);
        word2Map.put("Tom", 4);
        double expResult = Math.sqrt(33);
        double result = Main.EuclideanSimilarity(word1, word2, word1Map, word2Map);
        assertEquals(-expResult, result, 0.00001);
    }

    /**
     * Test of EuclideanSimilarityNormalized method, of class Main.
     */
    @Test
    public void testEuclideanSimilarityNormalized() {
        System.out.println("EuclideanSimilarityNormalized");
        String word1 = "man";
        String word2 = "liver";
        Map<String, Integer> word1Map = new HashMap<>();
        word1Map.put("cat", 5);
        word1Map.put("dog", 3);
        word1Map.put("John", 2);
        Map<String, Integer> word2Map = new HashMap<>();
        word2Map.put("cat", 2);
        word2Map.put("dog", 1);
        word2Map.put("Tom", 4);
        double expResult = 1.039042818;
        double result = Main.EuclideanSimilarityNormalized(word1, word2, word1Map, word2Map);
        assertEquals(-expResult, result, 0.00001);
    }
}
