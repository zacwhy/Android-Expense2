package com.example.expense;

public class SummaryListItem {

	private String label;
	private double amount;
	private double percentage;
	
	public SummaryListItem(String label, double amount, double percentage) {
		this.setLabel(label);
		this.setAmount(amount);
		this.setPercentage(percentage);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
}
