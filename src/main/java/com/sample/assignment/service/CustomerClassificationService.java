package com.sample.assignment.service;

import java.util.Calendar;
import java.util.Set;

import com.sample.assignment.model.ClassificationResponse;

public interface CustomerClassificationService {
	Set<Integer> getCustomerIds();
	ClassificationResponse classifyCustomer(int id, Calendar startDate, Calendar endDate);
}
