package com.example.common;

import java.util.Comparator;
import java.util.List;

//
// http://stackoverflow.com/a/3704930
//

public class MultiComparator<T> implements Comparator<T> {

    private final List<Comparator<T>> comparators;

    public MultiComparator(List<Comparator<T>> comparators) {
        this.comparators = comparators;
    }

    public int compare(T lhs, T rhs) {
        for (Comparator<T> comparator : comparators) {
            int comparison = comparator.compare(lhs, rhs);
            
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }
    
}
