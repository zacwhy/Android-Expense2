package com.example.expense.models;

import java.util.Comparator;

public class TransactionGroupDateComparator implements Comparator<TransactionGroup> {
    
    private int mod = 1;
    
    public TransactionGroupDateComparator(boolean descending) {
        if (descending) {
            mod =-1;
        }
    }
    
    @Override
    public int compare(TransactionGroup lhs, TransactionGroup rhs) {
        return mod * lhs.getDate().compareTo(rhs.getDate());
    }
    
}
