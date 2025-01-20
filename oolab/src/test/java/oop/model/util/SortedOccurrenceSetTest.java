package oop.model.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SortedOccurrenceSetTest {

    @Test
    public void testAddOrUpdateFirstTime() {
        SortedOccurrenceSet<String> sortedOccurrenceSet = new SortedOccurrenceSet<>();
        sortedOccurrenceSet.addOrUpdate("element");

        List<String> orderedElements = sortedOccurrenceSet.getOrderedElements();
        assertEquals(1, orderedElements.size(), "Size of orderedElements should be 1 after adding an element once");

        assertEquals("element", orderedElements.get(0), "First element in orderedElements should be the one added");

        assertEquals(1, sortedOccurrenceSet.getCount("element"), "Count of added element should be 1");
    }

    @Test
    public void testAddOrUpdateSecondTime() {
        SortedOccurrenceSet<String> sortedOccurrenceSet = new SortedOccurrenceSet<>();
        sortedOccurrenceSet.addOrUpdate("element");
        sortedOccurrenceSet.addOrUpdate("element");

        List<String> orderedElements = sortedOccurrenceSet.getOrderedElements();
        assertEquals(1, orderedElements.size(), "Size of orderedElements should still be 1 after adding same element twice");

        assertEquals("element", orderedElements.get(0), "First element in orderedElements should still be the one added");

        assertEquals(2, sortedOccurrenceSet.getCount("element"), "Count of added element should be 2");
    }

    @Test
    public void testAddOrUpdateThenRemoveElement() {
        SortedOccurrenceSet<String> sortedOccurrenceSet = new SortedOccurrenceSet<>();
        sortedOccurrenceSet.addOrUpdate("element");
        sortedOccurrenceSet.remove("element");

        List<String> orderedElements = sortedOccurrenceSet.getOrderedElements();
        assertTrue(orderedElements.isEmpty(), "orderedElements should be empty after removing the added element");

        assertEquals(0, sortedOccurrenceSet.getCount("element"), "Count of added element should be 0 after removing it");
    }
}