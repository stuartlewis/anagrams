package com.stuartlewis.trustpilot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Code to find anagram combinations for the TrustPilot code challenge
 * 
 * @see https://followthewhiterabbit.trustpilot.com/
 * 
 * @author Stuart Lewis
 */
public class DownTheRabbitHole {
    
    // The number of words to allow in an anagram
    private static final int MAX_WORDS = 3;
    
    // The given phrase to find anagrams from
    private static final String PHRASE = "poultryoutwitsants";
    
    // The wordlist URL
    private static final String WORDLIST_URL = "https://followthewhiterabbit.trustpilot.com/cs/wordlist";
    
    // Keep one copy of the MessageDigest code to avoid reinstantiation cost
    static MessageDigest md;
    
    
    /**
     * Run the script to find the TrustPilot code challenge anagrams
     * 
     * @param args None expected
     */            
    public static void main(String[] args) {
        // The list of words from the wordlist
        ArrayList<String> wordlist = new ArrayList<>();

        // Load the wordlist from TrustPilot
        try {
            URL downloadWordlist = new URL(WORDLIST_URL);
            BufferedReader in = new BufferedReader(new InputStreamReader(downloadWordlist.openStream(), "UTF8"));
            
            // Read the file line by line and add to the wordlist
            String line;
            while ((line = in.readLine()) != null) {
                // Record the word, and ensure it is lowercase for consistency
                wordlist.add(line.toLowerCase());
            }
            in.close();
        } catch (IOException ioEx) {
            System.err.println("Error downloading wordlist: " + ioEx.getMessage());
            System.exit(1);
        }
        
        // Perform the strip of words, so only using possible words for the anagram
        ArrayList<String> words = reduce(wordlist, PHRASE, true);
        
        // Dive down the rabbit hole on a recursive anagram adventure!
        HashMap<String, String> anagrams = dive(words, PHRASE, PHRASE, "", 0, new HashMap<>());
        
        // Let's see if we found the succesful anagrams?!
        System.out.println("Easy = " + anagrams.get("e4820b45d2277f3844eac66c903e84be"));
        System.out.println("Medium = " + anagrams.get("23170acc097c24edb98fc5488ab033fe"));
        System.out.println("Hardest = " + anagrams.get("665e5bcb0c20062fe8abaaf4628bb154"));
    }
    
    /**
     * A recursive dive down the word list.  Find a word to try as part of the anagram from the word list,
     * then get all possible words that could follow, and try each. Only go as deep as the MAX_WORDS allows.
     * 
     * E.g.
     *  - Level 1: ALL
     *  - Level 2: ALL ANOTHER
     *  - Level 3: ALL ANOTHER WORD
     *  - Level 3: ALL ANOTHER XRAY
     *  - Level 2: ALL NEXT
     *  - Level 3: ALL NEXT WORD
     *  - Level 3: ALL NEXT XRAY
     *  
     * @param candidateWords The candidate word list
     * @param phrase The phrase we are looking for
     * @param lettersLeft Letters left in the phrase to help reduce the search space (e.g. if the anagram is HELLO and the word HO has already been used, this will be ELL)
     * @param anagramSoFar What combination of words this tunnel is trying: E.g. "ALL ANOTHER"
     * @param depth The depth of tunnel we are in
     * @param anagrams The list of possible results: add to this as we go
     */
    static HashMap<String, String> dive(ArrayList<String> candidateWords, 
                                        String phrase, 
                                        String lettersLeft, 
                                        String anagramSoFar, 
                                        int depth,
                                        HashMap<String, String> anagrams) {
        // Record how deep we are so we know when to stop
        depth++;
        
        // Iterate through each possible word
        Iterator<String> candidateWordsIterator = candidateWords.iterator();
        while (candidateWordsIterator.hasNext()) {
            String word = candidateWordsIterator.next();
            String candidateAnagram = anagramSoFar + " " + word;
            
            // Display only the top level words to show code progress
            // and this is the first word so doesn't need the " " to have been added above between words
            if (depth == 1) {
                candidateAnagram = word;
                System.out.println(word);
            }
            
            // Have we found an anagram of the phrase? If so, record it
            if (isAnagram(candidateAnagram, phrase)) {
                anagrams.put(md5(candidateAnagram), candidateAnagram);
            } else {
                // Only go further if not yet at full depth,
                if (depth < MAX_WORDS) {
                    // Reduce the phrase we're looking for
                    String adjustedPhrase = removeWordFromPhrase(lettersLeft, word);
                    
                    // Make sure we're not over length to avoid unnecesary dives
                    if (adjustedPhrase.length() > 0) {
                        // Find the next set of words (no need to perform duplicate check as these were taken out first time round)
                        ArrayList<String> nextCandidateWords = reduce(candidateWords, adjustedPhrase, false);
                        
                        // Recurse again down a level...
                        anagrams = dive(nextCandidateWords, phrase, adjustedPhrase, candidateAnagram, depth, anagrams);
                    }
                }
            }
        }
        return anagrams;
    }
    
    /**
     * Calculate and return the MD5 hash of a string.
     * Perform this natively rather than via an external lIbrary to keep the code trim,
     * particularly if this were to be run via AWS lambda
     * 
     * @param toHash The String to hash
     * @return The returned MD5 hash value
     */
    static String md5(String toHash) {
        if (md ==  null) {
            // Initialise the MD5 message digest processor first time round
            try {
                md  = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nsaEx) {
                System.err.println("Error initialising MD5 message digest: " + nsaEx.getMessage());
                System.exit(1);
            }
        }
        BigInteger no = new BigInteger(1, (md.digest(toHash.getBytes())));
        String md5 = no.toString(16); 
        while (md5.length() < 32) { 
            md5 = "0" + md5; 
        }
        return md5;
    }
    
    /**
     * Method to decide if one string is an anagram of another
     * 
     * @param one The first word(s) to compare
     * @param two The second word(s) to compare
     * @return WHether or not this is an anagram
     */
    static boolean isAnagram(String one, String two) {
        // Strip spaces as there could be varying numbers of words
        one = one.replaceAll(" ", "");
        two = two.replaceAll(" ", "");
                
        // A cheap inital test of length, before the more expensive character-by-character test
        if (one.length() != two.length()) {
            return false;
        }
        
        // Check charachters one by one to see if they are there
        for (char c : one.toCharArray()) {
            if (two.contains(Character.toString(c))) {
                // Yes, and let's remove this to make future checks quicker
                two = two.replaceFirst(Character.toString(c), "");
            } else {
                // Missing character, no need to check the rest
                return false;
            }
        }
        
        // We got this far, so all characters present, we have a winner!
        return true;
    }
    
    /**
     * Small utility method to remove a set of characters from a phrase
     * In the anagram solver, this is used to reduce the phrase being searched.
     * For example if the phrase was "dlkgnveidsn" and the word "king" was being tested, 
     * this would return "dlvedsn"
     * 
     * Note: For speed, the code does not check that the word is actually included
     * in the phrase.  It is assumed this has already been done.
     * 
     * @param phrase The overall phrase being searched for (the original anagram)
     * @param word The candidate word to remove from it
     * @return The shortened new phrase to search
     */
    static String removeWordFromPhrase(String phrase, String word) {
        // Iterate through the word, and remove the first instance of each letter
        for (char c : word.toCharArray()) {
            phrase = phrase.replaceFirst(Character.toString(c), "");
        }
        
        // Return the shortened phrase
        return phrase;
    }
    
    /**
     * Reduce the candidate word list to make anagram searching more efficient.  A number of checks are performed on each candidate word in the original word list:
     *  - Drop words that are longer then the anagram phrase (cheap check, do it first)
     *  - If required (see duplicateCheck flag), remove duplicate words. Note: Only do this once at the beginning, as future runs are unnecessary
     *  - Finally do the expensive (but powerful) check of ensuring the word's characters are all contained in the phrase
     * 
     * @param candidates The list of candidate words
     * @param phrase The phrase to look for
     * @param duplicateCheck Whether to check for duplicates or not
     * @return The reduced set of candidate words
     */
    static ArrayList<String> reduce(ArrayList<String> candidates, String phrase, boolean duplicateCheck) {
        
        ArrayList<String> words = new ArrayList<String>();
        
        String candidate;
        Iterator<String> it = candidates.iterator();
        while (it.hasNext()) {
            candidate = it.next();
            
            // Drop out any words longer than the phrase - useful in second or third (or subsequent!) iterations 
            if (candidate.length() > phrase.length()) {
                continue;
            }
            
            // Do a quick duplicate check
            if ((duplicateCheck) && (words.contains(candidate))) {
                continue;
            }

            // Does the candidate word contain letters in the anagram phrase?
            String phrasePass = phrase;
            boolean keep = true;
            for (char c : candidate.toCharArray()) {
                if (phrasePass.contains(Character.toString(c))) {
                    // Yes, and let's remove this to make future checks quicker
                    phrasePass = phrasePass.replaceFirst(Character.toString(c), "");
                } else {
                    // Letter no included, so break from the checking loop and flag to not keep this word in the list
                    keep = false;
                    break;
                }
            }
            
            // If the word is flagged to keep, add it to the new candidate list
            if (keep) {
                words.add(candidate);
            }
        }
        
        // Return the new candidate list of words
        return words;
    }
}