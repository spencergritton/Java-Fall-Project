package edu.uiowa.cs.similarity;

import org.apache.commons.cli.*;
import opennlp.tools.stemmer.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");
        // Part 1
        options.addOption("s", false, "print words in sentences after corrections and number of sentences");

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
        ClassLoader cl = Main.class.getClassLoader();
        File sw = new File(cl.getResource("stopwords.txt").getFile());
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
    }
}
