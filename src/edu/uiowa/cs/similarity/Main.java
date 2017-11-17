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
        
        // PART ONE
        
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

        for (String item: sentences) {
            item = item.replaceAll("[^a-zA-Z0-9é'\\-\\s]", "");
            System.out.println(item);
        }
        
        //implement PorterStemmer to remove root words
        PorterStemmer stemmer = new PorterStemmer();
        
       

        
        //remove stop words found in stopwords.txt from sentence
        String [] roots = new String [] {"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could",
            "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further",
            "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers",
            "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it",
            "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once",
            "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
            "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", 
            "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up",
            "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's",
            "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've",
            "your", "yours", "yourself", "yourselves"};
        
        for (String root : roots) {
            if (sentences.contains(root)) {
                sentences.remove(root);
                System.out.println(sentences);
            }    
        }
    }
}
