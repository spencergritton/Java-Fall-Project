package edu.uiowa.cs.similarity;

import org.apache.commons.cli.*;
import opennlp.tools.stemmer.*;

import java.io.*;
import java.util.*;

// Spencer Gritton & Brady Hoskins

public class Main {
    
    public static TreeMap<String, Double> mapValuesSorted(Map<String, Double> map) {
        // uses ValueComparator to sort map by values
        Comparator<String> comparator = new MapComparator(map);
        TreeMap<String, Double> finalMap = new TreeMap<String, Double>(comparator);
        finalMap.putAll(map);
        return finalMap;
    }

    
    // PART 2, combine two hash maps
    public static Map<String, Double> mapCombiner(Map<String, Double> map1, Map<String, Double> map2) {
        
        Map<String, Double> tempMap = map1;
        // for each entry in map 2
        for (Map.Entry<String, Double> entry : map2.entrySet()) {
           
            // if entry not in map one then add it
            if (!tempMap.containsKey(entry.getKey())) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
            
            // otherwise it is in the map and must add the values together
            else {
                Double valueOfWord = tempMap.get(entry.getKey()) + entry.getValue();
                tempMap.put(entry.getKey(), valueOfWord);
            }
        }
        return tempMap;
    }
    
    public static Double CosineSimilarity(String word1, String word2, Map <String, Double> word1Map, Map <String, Double> word2Map) {
        // Calculate Numerator which is the summation of the multiplication of  count of each word that is in the vector of each word 
        Double Numerator = 0.0;
        
        // Now denometer must be found. which is the (summation of each word count^2 * other summation)^1/2
        
        // first need to find the summation for each word's words squared.
        // to do this use word1Map and word2Map args.
        Double word1SummationSquared = 0.0;
        Double word2SummationSquared = 0.0;
        
        // for each value in word1Map add the square of that value to it's summation
        for (Map.Entry<String, Double> entry : word1Map.entrySet()) {
            word1SummationSquared = word1SummationSquared + Math.pow(entry.getValue(), 2);
            // While iterating also calculate Numerator to save time
            // if word2Map contains this key that is in word one map
            if (word2Map.containsKey(entry.getKey())) {
                // Then add the multiplication of the number of times each occur to the Numerator summation.
                Numerator = Numerator + (entry.getValue()*word2Map.get(entry.getKey()));
            }
        }
        // for each value in word2Map add the square of that value to it's summation
        for (Map.Entry<String, Double> entry : word2Map.entrySet()) {
            word2SummationSquared = word2SummationSquared + Math.pow(entry.getValue(), 2);
        }
        
        Double Denominator = word1SummationSquared*word2SummationSquared;
        Denominator = Math.pow(Denominator, .5);
        
        if (Denominator.equals(0.0)) {
            return 0.0;
        }
        if (Numerator == 0) {
            return 0.0;
        }

        return (Numerator/Denominator);
    }
    
        // ES(a, b) = ( (a1-b1)^2 + (a2-b2)^2 + (a3-b3)^2 )^(1/2)
    public static double EuclideanSimilarity(String word1, String word2, Map <String, Double> word1Map, Map <String, Double> word2Map) {
        Double returnValue = 0.0;
        List<String> seenWords = new ArrayList<>();
        
        // for each item in word1 map, if the item is not word one
        // Then take (a1-b1) and add it to the return value
        // The problem with this is we also have to include when the word isn't in word1Map but IS in word2Map
        // so for each word we find in map1 append to a list and then when iterating through map 2 only include words 
        // Not in the list of words already seen.
        for (Map.Entry<String, Double> entry : word1Map.entrySet()) {
            seenWords.add(entry.getKey());
            
            if (word2Map.containsKey(entry.getKey())) {
                returnValue += Math.pow(entry.getValue()-word2Map.get(entry.getKey()), 2);
            }
            else {
                returnValue += Math.pow(entry.getValue(), 2);
            }
        }
        
        for (Map.Entry<String, Double> entry : word2Map.entrySet()) {
            // only add extra words if they aren't in word1Map but ARE in word2Map
            if (!seenWords.contains(entry.getKey())) {
                seenWords.add(entry.getKey());   
                
                // only words in here can be the ones not already added so word1Map vector of the word must be 0
                returnValue += Math.pow(entry.getValue(), 2);
            }
        }
        returnValue = Math.pow(returnValue, 0.5);
        returnValue = -returnValue;
        return returnValue;
    }
    
    // From the example in the PDF of Euclidean Similarity Normalized
    /*
    D% = (1,4,1,0,0,0) 
    D' = (3,0,0,1,1,2) 
    (1+16+1)^1/2
    18^1/2 = 4.242640687

    (9+1+1+4)^1/2
    15^(1/2) = 3.872983346

    -((1/4.242640687-3/3.872983346)^2+(4/4.242640687-0)^2+(1/4.242640687-0)^2+(0-1/3.872983346)^2+(0-1/3.872983346)^2+(0-2/3.872983346)^2)^(1/2) 
    = -1.278613166
*/
    
    public static double EuclideanSimilarityNormalized(String word1, String word2, Map <String, Double> word1Map, Map <String, Double> word2Map) {
        // First must find the normalization of word1Map vector and word2Map vector.
        // Do this by looping through each vector, adding up their counts^2 then squaring the entire number.
        Double word1Normalization = 0.0;
        Double word2Normalization = 0.0;
        
        for (Map.Entry<String, Double> entry : word1Map.entrySet()) {
            word1Normalization += Math.pow(entry.getValue(), 2);
        }
        word1Normalization = Math.pow(word1Normalization, 0.5);
        for (Map.Entry<String, Double> entry : word2Map.entrySet()) {
            word2Normalization += Math.pow(entry.getValue(), 2);
        }
        word2Normalization = Math.pow(word2Normalization, 0.5);
        
        // Now that normalizations are calculated proceed more or less exactly as EuclideanSimilarity EXCEPT
        // now it looks like ES(a,b) = ( (a1/anorm - b1/bnorm)^2 + (a2/anorm - b2/bnorm)^2 )^(1/2)
        
        Double returnValue = 0.0;
        List<String> seenWords = new ArrayList<>();
        
        // for each item in word1 map, if the item is not word one
        // Then take (a1-b1) and add it to the return value
        // The problem with this is we also have to include when the word isn't in word1Map but IS in word2Map
        // so for each word we find in map1 append to a list and then when iterating through map 2 only include words 
        // Not in the list of words already seen.
        for (Map.Entry<String, Double> entry : word1Map.entrySet()) {
            seenWords.add(entry.getKey());
            
            if (word2Map.containsKey(entry.getKey())) {
                returnValue += Math.pow((entry.getValue()/word1Normalization)-(word2Map.get(entry.getKey())/word2Normalization), 2);
            }
            else {
                returnValue += Math.pow((entry.getValue()/word1Normalization), 2);
            }
        }
        
        for (Map.Entry<String, Double> entry : word2Map.entrySet()) {
            // only add extra words if they aren't in word1Map but ARE in word2Map
            if (!seenWords.contains(entry.getKey())) {
                seenWords.add(entry.getKey());   
                
                // only words in here can be the ones not already added so word1Map vector of the word must be 0
                returnValue += Math.pow((entry.getValue()/word2Normalization), 2);
            }
        }
        returnValue = Math.pow(returnValue, 0.5);
        returnValue = -returnValue;
        return returnValue;
    }
    
    public static Pair closestClustering(Map<String, Map<String, Double>> VectorMap, Map<String, List<String>> Clusters, boolean usingCentroidVectorMap, Map<String, Map<String, Double>> centroidVectorMap) {
        // List to track the AVERAGE euclidean distance for each iteration
        List<Double> AverageEuclideanDistance = new ArrayList<>();
        // For each unique word in the text
        for (Map.Entry<String, Map<String, Double>> entry : VectorMap.entrySet()) { 
        // if the word is not a cluster point
            if (!Clusters.containsKey(entry.getKey())) {
        // Calculate the euclidean distance between the point and every cluster
                // Store the cluster String and it's euclidean distance to the "point" entry.getKey()
                Map<String, Double> tempCluster = new HashMap<>();
                // For each Cluster point
                for (Map.Entry<String, List<String>> innerEntry : Clusters.entrySet()) {
                    // Put the euclidean similarity of the cluster into the tempCluster map for later comparisons
                    if (!usingCentroidVectorMap) {
                        Double EuclideanDistance = EuclideanSimilarity(entry.getKey(), innerEntry.getKey(), entry.getValue(), VectorMap.get(innerEntry.getKey()));
                        AverageEuclideanDistance.add(EuclideanDistance);
                        tempCluster.put(innerEntry.getKey(), EuclideanDistance);
                    }
                    else {
                        Double EuclideanDistance = EuclideanSimilarity(entry.getKey(), innerEntry.getKey(), entry.getValue(), centroidVectorMap.get(innerEntry.getKey()));
                        AverageEuclideanDistance.add(EuclideanDistance);
                        tempCluster.put(innerEntry.getKey(), EuclideanDistance);
                    }
                }
        // Add the point to the cluster of the most similar word
                tempCluster = mapValuesSorted(tempCluster);
                boolean firstElement = true;
                for (Map.Entry<String, Double> innerEntry : tempCluster.entrySet()) {
                    if (firstElement) {
                        // innerEntry.getKey() is the cluster most similar to entry.getKey()
                        // So add entry.getKey() to innerEntry.getKey() 's cluster
                        List<String> tempList = Clusters.get(innerEntry.getKey());
                        tempList.add(entry.getKey());
                        Clusters.replace(innerEntry.getKey(), tempList);
                        firstElement = false;
                    }
                    break;
                }
            }
        }
        Double avgEucDistance = 0.0;
        for (Double item: AverageEuclideanDistance) {
            avgEucDistance += item;
        }
        avgEucDistance = avgEucDistance/AverageEuclideanDistance.size();
        
        Pair returnPair = new Pair(Clusters, avgEucDistance);
        
        return returnPair;
    }
    
    public static Map<String, List<String>> topClosestToCentroid(Map<String, Map<String, Double>> VectorMap, Map<String, List<String>> Clusters, Map<String, Map<String, Double>> centroidVectorMap, boolean usingCentroidMap, int topJ) {
        Map<String, Map<String, Double>> tempList = new HashMap<>();
        Map<String, List<String>> returnMap = new HashMap<>();
        
        // For each centroid
        for (Map.Entry<String, List<String>> eachCentroid: Clusters.entrySet()) {
            // Create new map to store how close each point is to the centroid
            Map<String, Double> innerEuclideanClosenessMap = new HashMap<>();

            if (!usingCentroidMap) {
                // Add the centroid itself because it is a word in it's own cluster
                innerEuclideanClosenessMap.put(eachCentroid.getKey(), 1.0);
            }

            // Calculate the closeness between each word and the centroid, and add to innerEuclideanClosenessMap
            // for each word in each centroid
            for (String word: eachCentroid.getValue()) {
                if (!usingCentroidMap) {
                    // add word and euclidan similarity to inner map.
                    innerEuclideanClosenessMap.put(word, EuclideanSimilarity(eachCentroid.getKey(), word, VectorMap.get(eachCentroid.getKey()), VectorMap.get(word)));
                }
                else {
                    // add word and euclidan similarity to inner map.
                innerEuclideanClosenessMap.put(word, EuclideanSimilarity(eachCentroid.getKey(), word, centroidVectorMap.get(eachCentroid.getKey()), VectorMap.get(word)));
                }
            }
            innerEuclideanClosenessMap = mapValuesSorted(innerEuclideanClosenessMap);
            
            if (innerEuclideanClosenessMap.size() > topJ) {
                    Map<String, Double> tempMap = new HashMap<>();
                    
                    for (Map.Entry<String, Double> entry : innerEuclideanClosenessMap.entrySet()) {
                        if (tempMap.size()!= topJ) {
                            tempMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    tempMap = mapValuesSorted(tempMap);
                    innerEuclideanClosenessMap.clear();
                    innerEuclideanClosenessMap.putAll(tempMap);
                }
            
            tempList.put(eachCentroid.getKey(), innerEuclideanClosenessMap);
        }
        
        // for each map in tempList, add centroid and all elements into returnMap for returning
        for (Map.Entry<String, Map<String, Double>> eachCentroid: tempList.entrySet()) {
            String centroidName = eachCentroid.getKey();
            List<String> cluster = new ArrayList<>();
            
            for (Map.Entry<String, Double> word: eachCentroid.getValue().entrySet()) {
                cluster.add(word.getKey());
            }
            returnMap.put(centroidName, cluster);
        }
        
        return returnMap;
    }
    
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
            .type(String.class)
            .desc("Print top J similarities to input word -t word,J")
            .required(false)
            .build();
        options.addOption(tOption);
        
        Option mOption = Option.builder("m")
            .longOpt("-m type of similarity measure")
            .numberOfArgs(1)
            .type(String.class)
            .desc("Decide what similarity measure top-j uses")
            .required(false)
            .build();
        options.addOption(mOption);
        
        String mOpt = "cosine";
        
        Option kOption = Option.builder("k")
            .numberOfArgs(1)
            .type(String.class)
            .desc("Print k clusters using iter, iterations... -k k,iter")
            .required(false)
            .build();
        options.addOption(kOption);
        
        Option jOption = Option.builder("j")
            .numberOfArgs(1)
            .type(String.class)
            .desc("Print top-j closest clusters to k clusters, iterating iter times.. -j k,iter,j")
            .required(false)
            .build();
        options.addOption(jOption);

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
        Map<String, Double> allWords = new HashMap<>();
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
                if (!allWords.containsKey(word)) {
                    allWords.put(word,0.0);
                }
            }
            sentencesList.add(sentenceInnerList);
        }
        
        if (cmd.hasOption("m")) {
            String mWord = cmd.getOptionValue("m");
            if (mWord.equals("euc")) {
                mOpt = "euc";
            }
            else if (mWord.equals("eucnorm")) {
                mOpt = "eucnorm";
            }
            else if (!mWord.equals("cosine")) {
                System.err.println("please input a valid similarity measure: cosine, euc, or eucnorm");
                System.exit(1);
            }
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
        Map<String, Map<String, Double>> VectorMap = new HashMap<>();
        // For each sentence in the sentences list
        for (List l: sentencesList) {
            // For each word in the sentence
            for (Object s: l) {
                // Create a map of all words in the sentence
                Map<String, Double> tempMap = new HashMap<>();
                // for each word in the sentence lookig at
                for (Object ss: l) {
                    // if it's the same as the word of the map looking at don't add it
                    if (ss.equals(s)) {
                        continue;
                    }
                    // otherwise add to the tempMap
                    // if item not in temp map add it
                    if (!tempMap.containsKey( (String) ss)) {
                        tempMap.put((String) ss, 1.0);
                        // if item in temp map add it
                    } else {
                        Double value = tempMap.get( (String) ss) + 1;
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
            for (Map.Entry<String, Map<String, Double>> entry : VectorMap.entrySet()) {
                System.out.println("Entry Word " + entry.getKey());
                // for each entry in each entry
                String built = "[";
                boolean tracker = false;
                for (Map.Entry<String, Double> innerEntry : entry.getValue().entrySet()) {
                    built += innerEntry.getKey() + "=" + innerEntry.getValue();
                    built += ", ";
                    tracker = true;
                }
                if (tracker == true) {
                    built = built.substring(0, built.length()-2);
                }
                built += "]";
                System.out.println(built);
                System.out.println("");
            }  
        }
        
        // If there is an option t then then must print the tInt most similar words to tWord.
        if (cmd.hasOption("t")) {
            
            String tWord = cmd.getOptionValue("t");
            String[] argArray = tWord.split(",");
            Double tInt = 0.0;

            try {
                // tWord is the word we are finding words most similar to
                // tInt is how many similar words we are finding
                tWord = argArray[0];
                tInt = Double.parseDouble(argArray[1]);
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                System.err.println("please use a valid input.. With no spaces between arguements");
                System.err.println("ex: -f\"filename\" -t cat,5");
                System.exit(1);
            }
            
            // if tWord not in VectorMap return error
            if (VectorMap.get(tWord) == null) {
                System.out.println("Cannot compute top-J similarity to " + tWord);
                
            } else {
            
                // Create a Map/Dict of all words and their cosine similarity to tWord
                Map<String, Double> SimMap = new HashMap<>();

                // The Vector of tWord
                Map <String, Double> tWordMap = VectorMap.get(tWord);

                // For each word vector entry calculate it's cosine similarity to tWord vector
                for (Map.Entry<String, Map<String, Double>> entry : VectorMap.entrySet()) {

                    if (entry.getKey().equals(tWord)) {
                        continue;
                    } else {
                    
                    //new code
                    //tword = man and entry.getKey() gets every word from the text in order to compare
                    Double Sim = 0.0;
                    
                    if (mOpt.equals("euc")) {
                        Sim = EuclideanSimilarity(tWord, entry.getKey(), tWordMap, entry.getValue());
                    }
                    else if (mOpt.equals("eucnorm")) {
                        Sim = EuclideanSimilarityNormalized(tWord, entry.getKey(), tWordMap, entry.getValue());
                    }
                    else {
                        Sim = CosineSimilarity(tWord, entry.getKey(), tWordMap, entry.getValue());
                    }
                    
                    SimMap.put(entry.getKey(), Sim);
                    }
                }
                // Sort cosSimMap to make most similar vectors appear at the start of the map
                SimMap = mapValuesSorted(SimMap);

                // if the map is bigger than desired size then add its elements to a temp map until it equals that size
                // then return the tempMap, else return the cosSimMap.
                if (SimMap.size() > tInt) {
                    Map<String, Double> tempMap = new HashMap<>();
                    
                    for (Map.Entry<String, Double> entry : SimMap.entrySet()) {
                        if (tempMap.size()!= tInt) {
                            tempMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    tempMap = mapValuesSorted(tempMap);
                    System.out.println(tempMap);
                } else {
                    System.out.println(SimMap);
                }
            }
        }
        
        
        // If there is an option k then then must print the k clusters of vector space using iter iterations
        // -k k,iter
        if (cmd.hasOption("k") || cmd.hasOption("j")) {
            
            int clusters = 0;
            int iterations = 0;
            int topJ = 0;
            
            if (cmd.hasOption("k")) {
                String kInput = cmd.getOptionValue("k");
                String[] argArray = kInput.split(",");

                try {
                // clusters is how many clusters
                // iterations is how many iterations of k-means to do
                clusters = Integer.parseInt(argArray[0]);
                iterations = Integer.parseInt(argArray[1]);
                }
                catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    System.err.println("please use a valid input.. With no spaces between arguements");
                    System.err.println("ex: -f\"filename\" -k 5,3 .. or -j 2,3,2");
                    System.exit(1);
                }
            }
            else {
                String jInput = cmd.getOptionValue("j");
                String[] argArray = jInput.split(",");
                try {
                    // clusters is how many clusters
                    // iterations is how many iterations of k-means to do
                    clusters = Integer.parseInt(argArray[0]);
                    iterations = Integer.parseInt(argArray[1]);
                    topJ = Integer.parseInt(argArray[2]);
                }
                catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    System.err.println("please use a valid input.. With no spaces between arguements");
                    System.err.println("ex: -f\"filename\" -k 5,3 .. or -j 2,3,2");
                    System.exit(1);
                }
            }
            
            // Print error if k # of clusters is set to 0.
            if (clusters == 0 || clusters > VectorMap.size()) {
                System.err.println("please input a valid k (# of clusters). Miniumum valid k = 1, Maximum valid k = every unique word in document");
                System.err.println("ex: -f\"filename\" -k 5,3 .. or -j 2,3,2");
                System.exit(1);
            }
            // Print error if iterations is set to 0
            if (iterations == 0) {
                System.err.println("please input a valid iters (# of iterations over k-means). Minimum valid iters = 1");
                System.err.println("ex: -f\"filename\" -k 5,3 .. or -j 2,3,2");
                System.exit(1);
            }
            
            // if cmd has option j and j is set to 0 print error
            if (cmd.hasOption("j") && topJ == 0) {
                System.err.println("please input a valid j value > 0");
                System.err.println("ex: -f\"filename\" -k 5,3 .. or -j 2,3,2");
                System.exit(1);
            }
            
            List<String> keysAsArray = new ArrayList<>(allWords.keySet());
            Random r = new Random();
            
            // Creates map of clusters and their clustered words.
            // {dog: [cat,cow,frog], mouse: [horse, person] }
            Map<String, List<String>> Clusters = new HashMap<>();
            Map<String, List<String>> tempClusters = new HashMap<>(); // used to store clusters between iterations
            
            // Generates "k" clusters of random words
            int i = 0;
            while (i < clusters) {
                int random = r.nextInt(keysAsArray.size());
                // if the item is not yet a cluster point add it
                if (!Clusters.containsKey(keysAsArray.get(random))) {
                    List<String> tempList = new ArrayList<>();
                    Clusters.put(keysAsArray.get(random), tempList);
                    i++;
                }
            }
            
            // Now cluster words have been chosen at random and we can begin to find which words go in which cluster
            // To find this we have to find the euclidean distance of every word to every cluster and put it in the 
            // cluster with the euclidean distance closest to 1.
            
            Pair clusteringPair = closestClustering(VectorMap, Clusters, false, null);
            Clusters = clusteringPair.mapPair;
            System.out.println("Iteration: 0");
            System.out.println("Average Distance: " + clusteringPair.doublePair);
            
            // if k-means must be calculated ITER (iterations) times.
            // in this case must calculate centroid's and recalculate clusters until have iterated over the cluster iter times.
            // only do this if iterations > 1, if iterations == 1 then simply return Clusters as the iteration
            if (iterations > 1) {
                int iterationCount = 1;
                // Vector map of all centroids
                Map<String, Map<String, Double>> centroidVectorMap = new HashMap<>();
                // while we still have to re-calculate k-means
                while (iterationCount < iterations) {
                    // First we must calculate the new centroids of each cluster. These will be the new clustering points
                    // To do this for loop over the "Clusters" Map
                    // To generate the name for each Cluster, calculate cluster number on with int clusterNumber
                    int clusterNumber = 1;
                    // For each cluster
                    // Two cases
                    // 1. if iterationCount = 1 meaning only the first k-random iteration has been done
                    // -----------BEGIN CALCULATION OF NEW CENTROIDS --------------------
                        // for each centroid in clusters, calculate a new centroid
                    for (Map.Entry<String, List<String>> entry: Clusters.entrySet()) {
                        // New Centroid
                        String centroidName = "Centroid: " + Integer.toString(clusterNumber); 
                        Map<String, Double> centroidValues = new HashMap<>();
                        // Since iteration count == 1 that means that Clusters map keys are actual words.
                        // These words should be in the new centroid so add all of the words values to the new centroid
                        // Put every element (vector) of current centroid (entry.getKey())) into the new centroid
                        if (iterationCount == 1) {
                            for (Map.Entry<String, Double> innerEntry: VectorMap.get(entry.getKey()).entrySet()) {
                                centroidValues.put(innerEntry.getKey(), innerEntry.getValue());
                            }
                        }

                        // Now must add every word from every vector in the old centroid to centroid values
                        // for each word in the current cluster
                        for (String wordInCluster: entry.getValue()) {
                            // Map<String, Double> wordVector = VectorMap.get(wordInCluster);
                            // for every word in the wordInCluster's vector, add it's value
                            // VectorEntry = {word: 1, word:5}
                            for (Map.Entry<String, Double> vectorEntry: VectorMap.get(wordInCluster).entrySet()) {
                                if (!centroidValues.containsKey(vectorEntry.getKey())) {
                                    centroidValues.put(vectorEntry.getKey(), vectorEntry.getValue());
                                }
                                else {
                                    centroidValues.put(vectorEntry.getKey(), centroidValues.get(vectorEntry.getKey()) + vectorEntry.getValue());
                                }
                            }
                        }

                        // Now all values and words have been added to the new centroid
                        // The last step is to simply divide each value by "r" to average out the centroid.
                        int rSize = entry.getValue().size() + 1; // this value is +1 to account for centroid "word" being a part of the new centroid
                        for (Map.Entry<String, Double> centroidEntry: centroidValues.entrySet()) {
                            centroidValues.replace(centroidEntry.getKey(), centroidEntry.getValue()/rSize);
                        }
                        // Add completed centroid to centroidVectorMap
                        centroidVectorMap.put(centroidName, centroidValues);
                        clusterNumber ++;
                    }
                    
                    iterationCount ++;
                    // ------------------------- END CALCULATION OF NEW CENTROIDS ----------------------
                    // Now we know for a fact that centroidVectorMap is full of "k" centroids.
                    // For each of these "k" new centroids we must find all the points that belong in their respective cluster
                    // To do this we must find the euclidean distance between every word and every centroid
                    // Then add each word to the centroid it is closest to.
                    Clusters.clear();
                    
                    // Clear out clusters because it must be filled with new mappings of {cluster, [word, word], ~~}
                    // First fill clusters with new cluster names
                    for (Map.Entry<String, Map<String, Double>> centroid: centroidVectorMap.entrySet()) {
                        List<String> tempList = new ArrayList<>();
                        Clusters.put(centroid.getKey(), tempList);
                    }
                    // Then cluster the points to their nearest centroid
                    clusteringPair = closestClustering(VectorMap, Clusters, true, centroidVectorMap);
                    Clusters = clusteringPair.mapPair;
                    
                    System.out.println("Iteration: " + (iterationCount-1));
                    System.out.println("Average Distance: " + clusteringPair.doublePair);
                    
                    tempClusters.clear();
                    tempClusters.putAll(Clusters);
                }
                // End of while (iterationCount < iterations    
                // Because of reclustering must print tempClusters
                if (cmd.hasOption("k")) {
                    System.out.println(tempClusters);
                }
                else {
                    System.out.println(topClosestToCentroid(VectorMap, tempClusters, centroidVectorMap, true, topJ));
                }
            }
            // End of if (iterations > 1)
            // Else meaning, if iterations == 1;
            else {
                if (cmd.hasOption("k")) {
                    System.out.println(Clusters); 
                }
                else {
                    System.out.println(topClosestToCentroid(VectorMap, tempClusters, null, false, topJ));
                }
            }
        }
    }
}
