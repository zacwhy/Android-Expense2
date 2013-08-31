package com.example.expense.models;

import java.math.BigDecimal;

public class SummaryListItem {

    private Object underlyingObject;
	private String label;
	private BigDecimal amount;
	
	public SummaryListItem(Object object, String label, BigDecimal amount) {
	    setUnderlyingObject(object);
		setLabel(label);
		setAmount(amount);
	}

	public Object getUnderlyingObject() {
        return underlyingObject;
    }

    public void setUnderlyingObject(Object underlyingObject) {
        this.underlyingObject = underlyingObject;
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
	
}
