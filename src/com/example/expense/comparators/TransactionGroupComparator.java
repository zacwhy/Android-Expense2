package com.example.expense.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.common.MultiComparator;
import com.example.expense.models.TransactionGroup;

public class TransactionGroupComparator implements Comparator<TransactionGroup> {
    
    private boolean descending;
    
    public TransactionGroupComparator(boolean descending) {
        this.descending = descending;
    }

    @Override
    public int compare(TransactionGroup lhs, TransactionGroup rhs) {
        List<Comparator<TransactionGroup>> comparatorList = new ArrayList<Comparator<TransactionGroup>>();
        
        comparatorList.add(new TransactionGroupDateComparator(descending));
        comparatorList.add(new TransactionGroupSequenceComparator(descending));
        
        MultiComparator<TransactionGroup> multiComparator = new MultiComparator<TransactionGroup>(comparatorList);
        
        return multiComparator.compare(lhs, rhs);
    }
    
}
