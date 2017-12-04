package edu.uiowa.cs.similarity.test.edu.uiowa.cs.similarity;

import edu.uiowa.cs.similarity.Main;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MainTest {
    
    public MainTest() {
    }

    /**
     * Test of mapValuesSorted method, of class Main.
     */
    @Test
    public void testMapValuesSorted() {
        System.out.println("mapValuesSorted");
        Map<String, Double> map = null;
        TreeMap<String, Double> expResult = null;
        TreeMap<String, Double> result = Main.mapValuesSorted(map);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mapCombiner method, of class Main.
     */
    @Test
    public void testMapCombiner() {
        System.out.println("mapCombiner");
        Map<String, Integer> map1 = null;
        Map<String, Integer> map2 = null;
        Map<String, Integer> expResult = null;
        Map<String, Integer> result = Main.mapCombiner(map1, map2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of CosineSimilarity method, of class Main.
     */
    @Test
    public void testCosineSimilarity() {
        System.out.println("CosineSimilarity");
        String word1 = "";
        String word2 = "";
        Map<String, Integer> word1Map = null;
        Map<String, Integer> word2Map = null;
        Double expResult = null;
        Double result = Main.CosineSimilarity(word1, word2, word1Map, word2Map);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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

    /**
     * Test of main method, of class Main.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        Main.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
