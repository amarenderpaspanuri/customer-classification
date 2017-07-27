package com.sample.assignment.entity;

import java.util.ArrayList;
import java.util.List;

public class Customer {
	private int id;
	private List<Transaction> transactions = new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}
}
