package com.example.expense.comparators;

import java.util.Comparator;

import com.example.expense.models.SummaryListItem;
import com.example.expense.models.TransactionGroup;

public class SummaryListItemComparator implements Comparator<SummaryListItem> {
    
    private boolean descending;
    
    public SummaryListItemComparator(boolean descending) {
        this.descending = descending;
    }

    @Override
    public int compare(SummaryListItem lhs, SummaryListItem rhs) {
        TransactionGroup t1 = (TransactionGroup) lhs.getUnderlyingObject();
        TransactionGroup t2 = (TransactionGroup) rhs.getUnderlyingObject();
        
        final Comparator<TransactionGroup> comparator = new TransactionGroupComparator(descending);
        return comparator.compare(t1, t2);
    }
    
}
