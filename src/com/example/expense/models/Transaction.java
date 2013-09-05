package com.example.expense.models;

import java.math.BigDecimal;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {

    private long id;
    private TransactionGroup transactionGroup;
    private int sequence;
    private Account toAccount;
    private BigDecimal amount;
    private String description;
    
    public Transaction(long id) {
        setId(id);
    }

    public Transaction(Parcel in) {
        setId(in.readLong());
        setDescription(in.readString());
        setAmount(new BigDecimal(in.readString()));
        setToAccount(new Account(in.readLong()));
    }

    public Transaction() {
        
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public TransactionGroup getTransactionGroup() {
        return transactionGroup;
    }

    public void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public static final Parcelable.Creator<Transaction> CREATOR =
            new Parcelable.Creator<Transaction>() {

        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getDescription());
        dest.writeString(getAmount().toPlainString());
        dest.writeLong(getToAccount().getId());
    }

}
