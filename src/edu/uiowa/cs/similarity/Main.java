package edu.uiowa.cs.similarity;

import org.apache.commons.cli.*;
import opennlp.tools.stemmer.*;

import java.io.*;
import java.util.*;

public class Main {
    
    // PART 2, combine two hash maps
    public static Map<String, Integer> mapCombiner(Map<String, Integer> map1, Map<String, Integer> map2) {
        
        Map<String, Integer> tempMap = map1;
        // for each entry in map 2
        for (Map.Entry<String, Integer> entry : map2.entrySet()) {
            
            // if entry not in map one then add it
            if (!tempMap.containsKey(entry.getKey())) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
            
            // otherwise it is in the map and must add the values together
            else {
                int valueOfWord = tempMap.get(entry.getKey()) + entry.getValue();
                tempMap.put(entry.getKey(), valueOfWord);
            }
        }
        return tempMap;
    }
    
    // PART 3, cosine similarity between two words with their given vectors
    public static double cosineSimilarity(String word1, String word2, Map<String, Integer> word1Vector, Map<String, Integer> word2Vector) {
        double returnValue = 0;
        
        
        return returnValue;
    }
    
    /*public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < vectorA.length; i++) {
        dotProduct += vectorA[i] * vectorB[i];
        normA += Math.pow(vectorA[i], 2);
        normB += Math.pow(vectorB[i], 2);
    }   
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}*/
    public static void main(String[] args) throws IOException {
        
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");
        // Part 1
        options.addOption("s", false, "print words in sentences after corrections and number of sentences");
        options.addOption("v", false, "print each word with all words it occurs with and frequency of occurance");

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("Main", options, true);
            System.exit(1);
        }

        String filename = cmd.getOptionValue("f");
		if (!new File(filename).exists()) {
			System.err.println("file does not exist "+filename);
			System.exit(1);
		}

        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        }
        
        // PART ONE -------------------------------------
        
        // Initializes file
        File temp = new File(filename);
        Scanner sc = new Scanner(temp);
        
        // Creates List of sentences from file
        List<String> sentences = new ArrayList<>();
        
        int index = 0;
        boolean startOfSentence = true;
        boolean endOfSentence = false;
        
        // While there are words in the file
        while (sc.hasNext()) {
            String item = sc.next();
            item = item.toLowerCase();
            
            // if item is a end of line turn endofline true
            if (item.contains(".") || item.contains("!") || item.contains("?")) {
                // Remove end of line punctuation
                item = item.replaceAll("[!?.\"]", "");
                endOfSentence = true;
            }
            
            // if start of sentence add to list
            if (startOfSentence) {
                sentences.add(item);
                startOfSentence = false;
            }
            
            // must not be start of sentence so add to a current
            else { sentences.set(index, sentences.get(index) + " " + item); }
            
            // if it's the end of sentence add to index, make start of sentence true and end of sentence false
            if (endOfSentence) {
                endOfSentence = false;
                startOfSentence = true;
                index ++;
            }
        }
        // Move all stopwords into a list
        ArrayList<String> stopWords = new ArrayList<>();
        File sw = new File("../stopwords.txt");
        Scanner stopSc = new Scanner(sw);
        
        while (stopSc.hasNext()) {
            stopWords.add(stopSc.next());
        }
        
        // Turn each sentence into an individual list [["hello, I'm, bill], ["i", "like", "cats"]]
        // ^^ list is "sentencesList"
        // Also stem each word and if the word is a stop word it is removed
        List<List<String>> sentencesList = new ArrayList<>();
        PorterStemmer stemmer = new PorterStemmer();
        
        // for each sentence
        index = 0;
        for (String item: sentences) {
            // Remove all extra characters and punctuation, except hyphens
            item = item.replaceAll("[^a-zA-Z0-9é'\\-\\s]","");
            sentences.set(index, item);
            index ++;
            
            List<String> sentenceInnerList = new ArrayList<>();
            // For each word in each string
            for (String word: item.split(" ")) {
                // if stop word remove it
                if (stopWords.contains(word)) {
                    continue;
                }
                // not a stop word so stem it and add to list
                word = stemmer.stem(word);
                sentenceInnerList.add(word);
            }
            sentencesList.add(sentenceInnerList);
        }
        
        // Adding command line option s for printing lines
        if (cmd.hasOption("s")) {
            String built = "[ ";
            index = 0;
            for (List<String> list: sentencesList) {
                built += "[";
                int innerIndex = 0;
               for (String item: list) {
                   if (innerIndex == 0) {built += item;}
                   else {built += ", " + item;}
                    innerIndex ++;
                }
               if (index == sentencesList.size() - 1) {built += "] ";}
               else {built += "], ";}
            index ++;
            }
            built += "]";
            System.out.println(built);
            System.out.println("Number of sentences: " + index);
        }
        
        // Semantic Descriptor Vectors Part 2
        // Dictionary containing keys (words) and values of those keys being other dictionaries
        // The value dictionary will contain all words in a sentence with said key and how many times they occur together
        
        // {"bill: {"is" : 3, "the" : 1, "illest": 2}, "man" : {"stuff" : 1} }
        
        Map<String, Map<String, Integer>> VectorMap = new HashMap<>();
        // For each sentence in the sentences list
        for (List l: sentencesList) {
            // For each word in the sentence
            for (Object s: l) {
                
                // Create a map of all words in the sentence
                Map<String, Integer> tempMap = new HashMap<>();
                // for each word in the sentence lookig at
                for (Object ss: l) {
                    // if it's the same as the word of the map looking at don't add it
                    if (ss.equals(s)) {
                        continue;
                    }
                    // otherwise add to the tempMap
                    // if item not in temp map add it
                    if (!tempMap.containsKey( (String) ss)) {
                        tempMap.put((String) ss, 1);
                        // if item in temp map add it
                    } else {
                        int value = tempMap.get( (String) ss) + 1;
                        tempMap.put((String) ss, value);
                    }
                }
                
                // Temp map for each word of sentence has been created, now must combine
                // Current map of each word (if there is one) with the temp map.
                
                // If the vector map doens't contain the key already then just add the key and make the value
                // The temp map
                if (!VectorMap.containsKey( (String) s)) {
                    VectorMap.put((String) s, tempMap);
                } 
                // other wise the key is already in the map so must combine temp map and current map
                else {
                    tempMap = mapCombiner( VectorMap.get(s), tempMap);
                    VectorMap.put((String) s, tempMap);
                }
            }
        }
        
        // Command line option v
        if (cmd.hasOption("v")) {
            // For each entry in VectorMap
            for (Map.Entry<String, Map<String, Integer>> entry : VectorMap.entrySet()) {
                System.out.println("Entry Word " + entry.getKey());
                // for each entry in each entry
                String built = "[";
                for (Map.Entry<String, Integer> innerEntry : entry.getValue().entrySet()) {

                    built += innerEntry.getKey() + "=" + innerEntry.getValue();
                    built += ", ";

                }
                built = built.substring(0, built.length()-2);
                built += "]";
                System.out.println(built);
                System.out.println("");
            }   
        }
    }
}
