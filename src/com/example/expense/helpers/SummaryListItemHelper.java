package com.example.expense.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.common.helpers.DateHelper;
import com.example.expense.comparators.SummaryListItemComparator;
import com.example.expense.models.SummaryListItem;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public final class SummaryListItemHelper {

    private SummaryListItemHelper() {}
    
    public static List<SummaryListItem> getListItems(List<TransactionGroup> transactionGroups) {
        List<SummaryListItem> items = new ArrayList<SummaryListItem>();
        
        for (TransactionGroup transactionGroup : transactionGroups) {
            SummaryListItem item = getListItem(transactionGroup);
            items.add(item);
        }
        
        return items;
    }
    
    public static SummaryListItem getListItem(TransactionGroup transactionGroup) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Transaction transaction : transactionGroup.getTransactions()) {
            totalAmount = totalAmount.add(transaction.getAmount());
        }
        
        String label = getLabel(transactionGroup);
        return new SummaryListItem(transactionGroup, label, totalAmount);
    }
    
    public static void sort(List<SummaryListItem> listItems) {
        Collections.sort(listItems, new SummaryListItemComparator(true));
    }
    
    private static String getLabel(TransactionGroup transactionGroup) {
        return DateHelper.getDateWithDayOfWeekString(transactionGroup.getDate()) + " " + 
                transactionGroup.getSequence() + " " + 
                getDescription(transactionGroup);
    }
    
    private static String getDescription(TransactionGroup transactionGroup) {
        if (transactionGroup.getTransactions().size() == 1) {
            Transaction transaction = transactionGroup.getTransactions().get(0);
            
            if (transaction.getDescription().isEmpty()) {
                String fromAccount = transactionGroup.getFromAccount().getName();
                String toAccount = transaction.getToAccount().getName();
                return fromAccount + " to " + toAccount;
            }
            
            return transaction.getDescription();
        }
        
        if (transactionGroup.getTransactions().size() > 1) {
            String expenseCategory = transactionGroup.getExpenseCategory().getTitle();
            String fromAccount = transactionGroup.getFromAccount().getName();
            int transactionCount = transactionGroup.getTransactions().size();
            return String.format("%s by %s (%s)", expenseCategory, fromAccount, transactionCount);
        }
        
        return "NO TRANSACTIONS?";
    }
    
}
