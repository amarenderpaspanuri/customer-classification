package com.sample.assignment.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassificationResponse {
	private int id;
	private Set<ClassifierType> classifications = new HashSet<>();
	private BigDecimal balance;
	private List<TransactionDetails> transactions = new ArrayList<>();;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<TransactionDetails> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionDetails> transactions) {
		this.transactions = transactions;
	}

	public Set<ClassifierType> getClassifications() {
		return classifications;
	}

	public void setClassifications(Set<ClassifierType> classifications) {
		this.classifications = classifications;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
