package com.example.expense.models;

public class ExpenseCategory {

    private long id;
    private String name;

    public ExpenseCategory(long id) {
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
