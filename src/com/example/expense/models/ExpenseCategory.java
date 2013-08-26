package com.example.expense.models;

public class ExpenseCategory {

	private long id;
	private String title;
	
	public ExpenseCategory(long id) {
	    setId(id);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
	
}
