package oop.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenomTest {
    private Genom genom;
    private List<Integer> genomData;

    @BeforeEach
    void setUp() {
        genomData = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        genom = new Genom(genomData);
    }

    @Test
    void testGenomCreation() {
        assertEquals(genomData, genom.getGenom());
        assertEquals(genomData.size(), genom.getGenomSize());
    }

    @Test
    void testGetGene() {
        for (int i = 0; i < genomData.size(); i++) {
            assertEquals(genomData.get(i).intValue(), genom.getGene(i));
        }
    }

    @Test
    void testRandomGenomCreation() {
        int genomLength = 8;
        Genom randomGenom = Genom.getRandomGenom(genomLength);

        assertEquals(genomLength, randomGenom.getGenomSize());

        // Check if all genes are within valid range (0-7)
        for (int i = 0; i < genomLength; i++) {
            int gene = randomGenom.getGene(i);
            assertTrue(gene >= 0 && gene < 8);
        }
    }

    @RepeatedTest(5) // Run multiple times to test randomness
    void testGetRandomGene() {
        int genomLength = 8;
        int randomGene = Genom.getRandomGene(genomLength);
        assertTrue(randomGene >= 0 && randomGene < genomLength);
    }

    @Test
    void testGenomForChild_FatherDominant() {
        List<Integer> fatherGenomData = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1);
        List<Integer> motherGenomData = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0);
        Genom fatherGenom = new Genom(fatherGenomData);
        Genom motherGenom = new Genom(motherGenomData);

        // Father has 75% of total energy
        int fatherEnergy = 75;
        int motherEnergy = 25;

        Genom childGenom = Genom.genomForChild(fatherGenom, motherGenom, fatherEnergy, motherEnergy);

        assertEquals(fatherGenomData.size(), childGenom.getGenomSize());
        assertNotNull(childGenom.getGenom());
    }

    @Test
    void testGenomForChild_MotherDominant() {
        List<Integer> fatherGenomData = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1);
        List<Integer> motherGenomData = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0);
        Genom fatherGenom = new Genom(fatherGenomData);
        Genom motherGenom = new Genom(motherGenomData);

        // Mother has 75% of total energy
        int fatherEnergy = 25;
        int motherEnergy = 75;

        Genom childGenom = Genom.genomForChild(fatherGenom, motherGenom, fatherEnergy, motherEnergy);

        assertEquals(motherGenomData.size(), childGenom.getGenomSize());
        assertNotNull(childGenom.getGenom());
    }

    @Test
    void testGenomForChild_EqualEnergy() {
        List<Integer> fatherGenomData = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1);
        List<Integer> motherGenomData = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0);
        Genom fatherGenom = new Genom(fatherGenomData);
        Genom motherGenom = new Genom(motherGenomData);

        // Equal energy
        int fatherEnergy = 50;
        int motherEnergy = 50;

        Genom childGenom = Genom.genomForChild(fatherGenom, motherGenom, fatherEnergy, motherEnergy);

        assertEquals(fatherGenomData.size(), childGenom.getGenomSize());
        assertNotNull(childGenom.getGenom());
    }

    @RepeatedTest(5) // Run multiple times to test randomness
    void testGenomMutation() {
        int minMutateGene = 1;
        int maxMutateGene = 3;

        Genom mutatedGenom = Genom.genomMutation(genom, minMutateGene, maxMutateGene);

        // Check size remains the same
        assertEquals(genom.getGenomSize(), mutatedGenom.getGenomSize());

        // Count differences between original and mutated genom
        int differences = 0;
        for (int i = 0; i < genom.getGenomSize(); i++) {
            if (genom.getGene(i) != mutatedGenom.getGene(i)) {
                differences++;
                // Verify mutated gene is within valid range
                assertTrue(mutatedGenom.getGene(i) >= 0 && mutatedGenom.getGene(i) < 8);
            }
        }

        // Verify number of mutations is within specified range
        System.out.println(differences);
        System.out.println(mutatedGenom);
        System.out.println(genom);
        assertTrue(differences >= minMutateGene && differences <= maxMutateGene);
    }

    @Test
    void testGenomMutation_NoMutation() {
        // Test when min and max mutation are 0
        Genom mutatedGenom = Genom.genomMutation(genom, 0, 0);

        // Verify the genome remains unchanged
        for (int i = 0; i < genom.getGenomSize(); i++) {
            assertEquals(genom.getGene(i), mutatedGenom.getGene(i));
        }
    }

    @Test
    void testGenomMutation_AllGenes() {
        // Test mutation of all genes
        int genomSize = genom.getGenomSize();
        Genom mutatedGenom = Genom.genomMutation(genom, genomSize, genomSize);

        // Verify all genes are within valid range
        for (int i = 0; i < genomSize; i++) {
            int gene = mutatedGenom.getGene(i);
            assertTrue(gene >= 0 && gene < 8);
        }
    }
}