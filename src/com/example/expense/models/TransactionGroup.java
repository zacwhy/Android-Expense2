package com.example.expense.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionGroup {

    private long id;
    private Calendar date;
    private int sequence;
    private ExpenseCategory expenseCategory;
    private Account fromAccount;
    private List<Transaction> transactionList;
    
    public TransactionGroup(long id) {
        this();
        setId(id);
    }
    
    public TransactionGroup() {
        transactionList = new ArrayList<Transaction>();
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }
    
    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }
    
    public List<Transaction> getTransactions() {
        return transactionList;
    }
    
    public void setTransactions(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

}
