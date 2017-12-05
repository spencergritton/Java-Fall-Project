
package edu.uiowa.cs.similarity;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class MainTest {
    
    public MainTest() {
    }
    /**
     * Test of EuclideanSimilarity method, of class Main.
     */
    @Test
    public void testEuclideanSimilarity() {
        System.out.println("EuclideanSimilarity");
        String word1 = "";
        String word2 = "";
        Map<String, Integer> word1Map = null;
        Map<String, Integer> word2Map = null;
        double expResult = 0.0;
        double result = Main.EuclideanSimilarity(word1, word2, word1Map, word2Map);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of EuclideanSimilarityNormalized method, of class Main.
     */
    @Test
    public void testEuclideanSimilarityNormalized() {
        System.out.println("EuclideanSimilarityNormalized");
        String word1 = "";
        String word2 = "";
        Map<String, Integer> word1Map = null;
        Map<String, Integer> word2Map = null;
        double expResult = 0.0;
        double result = Main.EuclideanSimilarityNormalized(word1, word2, word1Map, word2Map);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
