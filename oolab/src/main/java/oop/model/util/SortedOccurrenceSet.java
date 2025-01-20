package oop.model.util;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class SortedOccurrenceSet<T> {
    private final Map<T, Integer> elementCounts = new ConcurrentHashMap<>();
    private final ConcurrentSkipListMap<Integer, LinkedHashSet<T>> countToElements = new ConcurrentSkipListMap<>();;

    public synchronized void addOrUpdate(T element) {
        int oldCount = elementCounts.getOrDefault(element, 0);
        int newCount = oldCount + 1;

        elementCounts.put(element, newCount);

        if (oldCount > 0) {
            LinkedHashSet<T> oldSet = countToElements.get(oldCount);
            oldSet.remove(element);
            if (oldSet.isEmpty()) {
                countToElements.remove(oldCount);
            }
        }

        countToElements.computeIfAbsent(newCount, k -> new LinkedHashSet<>()).add(element);
    }

    public synchronized void remove(T element) {
        if (!elementCounts.containsKey(element)) {
            return;
        }

        int oldCount = elementCounts.get(element);

        elementCounts.remove(element);

        LinkedHashSet<T> oldSet = countToElements.get(oldCount);
        oldSet.remove(element);
        if (oldSet.isEmpty()) {
            countToElements.remove(oldCount);
        }
    }

    public synchronized List<T> getOrderedElements() {
        List<T> result = new ArrayList<>();
        for (LinkedHashSet<T> elements : countToElements.values()) {
            result.addAll(elements);
        }
        return result;
    }

    public synchronized T getFirst() {
        if (countToElements.isEmpty()) {
            return null;
        }
        return countToElements.get(countToElements.lastKey()).iterator().next();
    }

    public synchronized List<T> getTopThree() {
        List<T> topThree = new ArrayList<>();
        // Traverse in descending order of counts
        for (Map.Entry<Integer, LinkedHashSet<T>> entry : countToElements.descendingMap().entrySet()) {
            for (T element : entry.getValue()) {
                if (topThree.size() >= 3) {
                    return topThree;
                }
                topThree.add(element);
            }
        }
        return topThree;
    }

    public synchronized int getCount(T element) {
        return elementCounts.getOrDefault(element, 0);
    }
}