package com.example.expense.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.common.MultiComparator;

public class TransactionGroupListComparator implements Comparator<TransactionGroup> {
    
    private boolean descending;
    
    public TransactionGroupListComparator(boolean descending) {
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