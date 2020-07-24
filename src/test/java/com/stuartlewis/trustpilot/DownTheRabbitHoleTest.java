package com.stuartlewis.trustpilot;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for DownTheRabbitHole
 * 
 * @author Stuart Lewis
 */
public class DownTheRabbitHoleTest {
    
    /**
     * Test the method which looks to see if two words are true anagrams
     */
    @Test
    public void isAnagram() {
        assertTrue(DownTheRabbitHole.isAnagram("trustpilot", "toliptsurt"), "The same letters should be an anagram");
        assertFalse(DownTheRabbitHole.isAnagram("trustpilot", "somethingelse"), "Different letters should not be an anagram");
        assertTrue(DownTheRabbitHole.isAnagram("trustpilot", "to lip tsurt"), "The same letters should be an anagram including spaces");
    }
    
    /**
     * Test the MD5 hash generation
     */
    @Test
    public void md5() {
        assertEquals(DownTheRabbitHole.md5("trustpilot"), "f5ad76ee64505e3ae887b634e05f30be", "trustpilot as an MD5 hash should be f5ad76ee64505e3ae887b634e05f30be");
        assertNotEquals(DownTheRabbitHole.md5("trustpilot"), "X5ad76ee64505e3ae887b634e05f30bX", "trustpilot as an MD5 hash should be f5ad76ee64505e3ae887b634e05f30be"); 
    }
    
    /**
     * Test the method that removes words from phrases once a candidate word has been chosen
     */
    @Test
    public void removeWordFromPhrase() {
        assertEquals(DownTheRabbitHole.removeWordFromPhrase("myphrase", "phrase"), "my", "Removing phrase from myphrase should leave my");
        assertNotEquals(DownTheRabbitHole.removeWordFromPhrase("myphrase", "phrase"), "phrase", "Removing phrase from myphrase should leave my");
    }
    
    /**
     * Test the candidate reduction code
     */
    @Test
    public void reduce() {
        // The candidate words
        ArrayList<String> testWords = new ArrayList<>();
        testWords.add("this");
        testWords.add("this"); // Added twice to check duplciates are removed
        testWords.add("is");
        testWords.add("a");
        testWords.add("unit");
        testWords.add("test");
        
        // Test phrase: unit test backwards
        String phrase = "tsettniu";
        
        // The words that could be in the anagram
        ArrayList<String> possibles = new ArrayList<>();
        possibles.add("is");
        possibles.add("unit");
        possibles.add("test");
        
        assertEquals(DownTheRabbitHole.reduce(testWords, phrase, true), possibles, "We expect to see is / unit / test");
    }
}
