package com.sample.assignment.entity;

import java.math.BigDecimal;
import java.util.Calendar;

public class Transaction implements Comparable<Transaction> {
	private Calendar time;
	private BigDecimal amount;
	private String description;
	private TransactionType type;

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
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

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	@Override
	public int compareTo(Transaction o) {
		return time.compareTo(o.getTime());
	}
}
