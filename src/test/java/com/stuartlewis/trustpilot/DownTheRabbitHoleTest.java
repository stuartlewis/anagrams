package com.stuartlewis.trustpilot;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for DownTheRabbitHole
 * 
 * @author Stuart Lewis
 */
public class DownTheRabbitHoleTest {
    @Test
    public void isAnagram() {
        assertTrue(DownTheRabbitHole.isAnagram("trustpilot", "toliptsurt"), "The same letters should be an anagram");
        assertFalse(DownTheRabbitHole.isAnagram("trustpilot", "somethingelse"), "Different letters should not be an anagram");
        assertTrue(DownTheRabbitHole.isAnagram("trustpilot", "to lip tsurt"), "The same letters should be an anagram including spaces");
    }
    
    @Test
    public void md5() {
        assertEquals(DownTheRabbitHole.md5("trustpilot"), "f5ad76ee64505e3ae887b634e05f30be", "trustpilot as an MD5 hash should be f5ad76ee64505e3ae887b634e05f30be");
    }
}
