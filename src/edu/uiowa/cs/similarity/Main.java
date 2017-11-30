package edu.uiowa.cs.similarity;

import org.apache.commons.cli.*;
import opennlp.tools.stemmer.*;

import java.io.*;
import java.util.*;

public class Main {
    
    public static TreeMap<String, Double> mapValuesSorted(Map<String, Double> map) {
        // uses ValueComparator to sort map by values
        Comparator<String> comparator = new MapComparator(map);
        TreeMap<String, Double> finalMap = new TreeMap<String, Double>(comparator);
        finalMap.putAll(map);
        return finalMap;
    }

    
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
    
    //Start Brady Code
    //Right now word=man and map2 = all the entries
    /*public static double cosineSimilarity(String word1, String word2) {
        double dotProduct = 0;
        double normVect1 = 0;
        double normVect2 = 0;
        
        int count = 0;
        System.out.println(word1);
        System.out.println(word2);
        //need to check 
        
        for (Map.Entry<String, Integer> entry : map2.entrySet()) {
            //System.out.println(entry);
            //System.out.println(entry.getKey());
            if(entry.getKey().contains(word1)) {
                count += 1;
            }
            if (count >= 1) {
                System.out.println(count);
            }
            System.out.println(count);
        }
        return 0;
    }*/
  
    //Part 3 Cosine Similarity Method
    public static Map<String, Integer> cosineTermMapHelper(String[] Terms) {
        Map<String, Integer> cosineTermMap = new HashMap<>();
        for (String term : Terms) {
            Integer n = cosineTermMap.get(term);
            n = (n == null) ? 1 : ++n;
            cosineTermMap.put(term, n);
        }
        return cosineTermMap;
    }
    
    
    //word1 is "dog" word2 is each other word from the entry vector
    public static double cosineSim (String word1, String word2) {
        Map<String, Integer> u = cosineTermMapHelper(word1.split("\\W+"));
        Map<String, Integer> v = cosineTermMapHelper(word2.split("\\W+"));
        
        //System.out.println(word1);
        //System.out.println(word2);
        
        //System.out.println(u);
        //System.out.println(v); 
        
        //System.out.println(u.keySet());
        //System.out.println(v.keySet());
        
        HashSet<String> same = new HashSet<>(u.keySet());
        same.retainAll(v.keySet());
        
        double dotProduct = 0, magU =0, magV = 0;
        
        for(String item : same) {
            //System.out.println(item);
            dotProduct += u.get(item) * v.get(item);
            //System.out.println(dotProduct);
        } 
        
        for (String x : u.keySet()) {
            //System.out.println(x);
            magU += Math.pow(u.get(x), 2);
            //System.out.println(magU);
        }
        
        for (String y : v.keySet()) {
            //System.out.println(y);
            magV += Math.pow(v.get(y), 2);
            //System.out.println(magV);
        }
        
        //System.out.println(dotProduct / Math.sqrt(magU * magV));
        return dotProduct / Math.sqrt(magU * magV);
    }
    
    //End Brady Code
    
    public static void main(String[] args) throws IOException {
        
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");
        // Part 1
        options.addOption("s", false, "print words in sentences after corrections and number of sentences");
        options.addOption("v", false, "print each word with all words it occurs with and frequency of occurance");
        
        // DON'T PUT A SPACE BETWEEN ARGUEMENTS, CODE EXAMPLE DOES NOT INCLUDE SPACE BETWEEN ARGS
        Option tOption = Option.builder("t")
                         .longOpt("word,number")
                         .numberOfArgs(1)
                         .required(true)
                         .type(String.class)
                         .desc("")
                         .build();

        
        options.addOption(tOption);

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
                
        String tWord = cmd.getOptionValue("t");
        String[] argArray = tWord.split(",");
        
        // tWord is the word we are finding words most similar to
        // tInt is how many similar words we are finding
        tWord = argArray[0];
        int tInt = Integer.parseInt(argArray[1]);


        if (cmd.getOptionValue("t") == null) {
            System.err.println("t options don't exist");
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
            item = item.replaceAll("[^a-zA-Z0-9Ã©'\\-\\s]","");
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
        
        // If there is an option t then then must print the tInt most similar words to tWord.
        if (cmd.hasOption("t")) {
            
            // if tWord not in VectorMap return error
            if (VectorMap.get(tWord) == null) {
                System.out.println("Cannot compute top-J similarity to " + tWord);
                
            } else {
            
                // Create a Map/Dict of all words and their cosine similarity to tWord
                Map<String, Double> cosSimMap = new HashMap<>();

                // The Vector of tWord
                Map <String, Integer> tWordMap = VectorMap.get(tWord);

                // For each word vector entry calculate it's cosine similarity to tWord vector
                for (Map.Entry<String, Map<String, Integer>> entry : VectorMap.entrySet()) {

                    // This part with random is just to test the sorting algorithmn
                    Random r = new Random(); 
                    Double d = r.nextDouble();

                    Double cosineSim = d;
                    // cosineSim = COSINE SIMILARITY FUNCTION GOES HERE
                    // Should output cosine similarity between tWord and entry.getValue() (gives map of entry.getKey() aka comparison word
                    
                    //new code
                    //tword = man and entry.getKey() gets every word from the text in order to compare
                    cosineSim = cosineSim(tWord, entry.getKey());
                    
                    cosSimMap.put(entry.getKey(), cosineSim);
                   
                }
                // Sort cosSimMap to make most similar vectors appear at the start of the map
                cosSimMap = mapValuesSorted(cosSimMap);

                // Make the return map not include the 0 index (as that is the most similar element)
                // Also make it only return "tInt" amount of elements.
                cosSimMap.remove(tWord);
                
                // if the map is bigger than desired size then add its elements to a temp map until it equals that size
                // then return the tempMap, else return the cosSimMap.
                if (cosSimMap.size() > tInt) {
                    Map<String, Double> tempMap = new HashMap<>();
                    
                    for (Map.Entry<String, Double> entry : cosSimMap.entrySet()) {
                        if (tempMap.size()!= tInt) {
                            tempMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    tempMap = mapValuesSorted(tempMap);
                    System.out.println(tempMap);
                } else {
                    System.out.println(cosSimMap);
                }
            }
        }
    }
}
