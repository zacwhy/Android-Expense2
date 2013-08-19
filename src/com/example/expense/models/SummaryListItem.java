package com.example.expense.models;

import java.math.BigDecimal;

public class SummaryListItem {

	private String label;
	private BigDecimal amount;
	private double percentage;
	
	public SummaryListItem(String label, BigDecimal amount, double percentage) {
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
}
