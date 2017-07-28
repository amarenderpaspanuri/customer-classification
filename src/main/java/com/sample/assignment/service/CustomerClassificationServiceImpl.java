package com.sample.assignment.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.sample.assignment.entity.Customer;
import com.sample.assignment.entity.Transaction;
import com.sample.assignment.entity.TransactionType;
import com.sample.assignment.model.ClassificationResponse;
import com.sample.assignment.model.ClassifierType;
import com.sample.assignment.model.TransactionDetails;

@Service
public class CustomerClassificationServiceImpl implements CustomerClassificationService {
	private Map<Integer, Customer> customers = new HashMap<>();
	private final String DATE_TIME_FORMAT = "d/MM/yyyy hh:mm:ss a";
	
	@PostConstruct
	public void init() {
		File file = new File(getClass().getClassLoader().getResource("data.txt").getFile());
		try (Scanner scanner = new Scanner(file)) {
			String line = "";
			boolean headerLine = true;
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (headerLine) {
					headerLine = false;
					continue;
				}
				String[] data = line.split(",");
				int id = Integer.valueOf(data[0]);
				Customer customer = customers.get(id);
				if (customer == null) {
					customer = new Customer();
					customer.setId(id);
					customers.put(id, customer);
				}
				Transaction transaction = new Transaction();
				SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
				Calendar time = Calendar.getInstance();
				time.setTime(formatter.parse(data[1]));
				transaction.setTime(time);
				String amount = data[2];
				if (amount.startsWith("-")) {
					transaction.setType(TransactionType.DEBIT);
					transaction.setAmount(new BigDecimal(amount.substring(1)));
				} else {
					transaction.setType(TransactionType.CREDIT);
					transaction.setAmount(new BigDecimal(amount));
				}
				transaction.setDescription(data[3]);
				customer.getTransactions().add(transaction);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} 
		
	}

	@Override
	public Set<Integer> getCustomerIds() {
		Set<Integer> set = new HashSet<>();
		for(Map.Entry<Integer, Customer> entry : customers.entrySet()) {
			if(!set.contains(entry.getKey())) {
				set.add(entry.getKey());
			}
		}
		return set;
	}
	
	public ClassificationResponse classifyCustomer(int id, Calendar startDate, Calendar endDate) {
		ClassificationResponse response = new ClassificationResponse();
		Customer customer = customers.get(id);
		int totalTransactions = 0;
		int beforeMiddayTransactions = 0;
		int afterMiddayTransactions = 0;
		BigDecimal totalDeposits = BigDecimal.ZERO;
		BigDecimal totalSpendings = BigDecimal.ZERO;
		Calendar depositStartDate = null;
		BigDecimal amountSpentInSevenDaysAfterDeposit = BigDecimal.ZERO;
		if (customer != null) {
			response.setId(customer.getId());
			for (Transaction transaction : customer.getTransactions()) {
				if (transaction.getTime().before(startDate) || transaction.getTime().after(endDate)) {
					continue;
				}
				TransactionDetails transactionDetails = new TransactionDetails();
				transactionDetails.setType(transaction.getType().name());
				transactionDetails.setDescription(transaction.getDescription());
				SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
				transactionDetails.setTime(formatter.format(transaction.getTime().getTime()));
				transactionDetails.setAmount(transaction.getAmount().toPlainString());
				response.getTransactions().add(transactionDetails);
				
				if (transaction.getType() == TransactionType.DEBIT) {
					totalSpendings = totalSpendings.add(transaction.getAmount());
				} else {
					totalDeposits = totalDeposits.add(transaction.getAmount());
					depositStartDate = transaction.getTime();
					amountSpentInSevenDaysAfterDeposit = BigDecimal.ZERO;
				}
				
				// Makes one or more withdrawals over $1000 in the month
				if (transaction.getType() == TransactionType.DEBIT &&
						transaction.getAmount().compareTo(new BigDecimal("1000")) >= 0) {
					response.getClassifications().add(ClassifierType.BIG_TICKET_SPENDER);
				}
				
				if (depositStartDate != null) {
					if (transaction.getType() == TransactionType.DEBIT &&
							daysDifference(depositStartDate, transaction.getTime()) <= 7) {
						amountSpentInSevenDaysAfterDeposit = amountSpentInSevenDaysAfterDeposit.add(transaction.getAmount());
					}
					
					// Spends over 75% of any deposit within 7 days of making it
					if ((totalDeposits.longValue() > 0) 
							&& ((amountSpentInSevenDaysAfterDeposit.longValue() * 100)/totalDeposits.longValue()) > 75) {
						response.getClassifications().add(ClassifierType.FAST_SPENDER);
					}
				}
				
				Calendar middayTime = Calendar.getInstance();
				middayTime.setTime(transaction.getTime().getTime());
				middayTime.set(Calendar.HOUR_OF_DAY, 12);
				middayTime.set(Calendar.MINUTE, 0);
				middayTime.set(Calendar.SECOND, 0);
				if (transaction.getTime().before(middayTime)) {
					beforeMiddayTransactions++;
				} else {
					afterMiddayTransactions++;
				}
				totalTransactions++;
			}
			
			// Makes over 50% of their transactions in the month after midday
			if (totalTransactions > 0 &&
					((afterMiddayTransactions * 100)/ totalTransactions) >= 50) {
				response.getClassifications().add(ClassifierType.AFTER_NOON_PERSON);
			}
			
			// Makes over 50% of their transactions in the month before midday
			if (totalTransactions > 0 &&
					((beforeMiddayTransactions * 100)/ totalTransactions) < 50) {
				response.getClassifications().add(ClassifierType.MORNING_PERSON);
			}
			
			// Spends over 80% of their deposits every month
			if ((totalDeposits.longValue() > 0) 
					&& ((totalSpendings.longValue() * 100)/totalDeposits.longValue()) >= 80) {
				response.getClassifications().add(ClassifierType.BIG_SPENDER);
			}
			
			// Spends less than 25% of their deposits every month ($ value of deposits)
			if ((totalDeposits.longValue() > 0) 
					&& ((totalSpendings.longValue() * 100)/totalDeposits.longValue()) <= 25
					&& ((totalSpendings.longValue() * 100)/totalDeposits.longValue()) > 0) {
				response.getClassifications().add(ClassifierType.POTENTIAL_SAVER);
			}
			
			// If a person is identified as both a Big Spender and a Fast Spender then they should be classified as a
			// Potential Loan customer instead.
			if (response.getClassifications().contains(ClassifierType.BIG_SPENDER) &&
					response.getClassifications().contains(ClassifierType.FAST_SPENDER)) {
				response.getClassifications().add(ClassifierType.POTENTIAL_LOAN);
				response.getClassifications().remove(ClassifierType.BIG_SPENDER);
				response.getClassifications().remove(ClassifierType.FAST_SPENDER);
			}
			
			response.setBalance(totalDeposits.subtract(totalSpendings));
		}
		return response;
	}
	
	private int daysDifference(Calendar calendar, Calendar other) {
		Calendar dayOne = (Calendar) calendar.clone();
		Calendar dayTwo = (Calendar) other.clone();
		dayOne.set(Calendar.HOUR_OF_DAY, 0);
		dayOne.set(Calendar.MINUTE, 0);
		dayOne.set(Calendar.SECOND, 0);
		dayOne.set(Calendar.MILLISECOND, 0);
		dayTwo.set(Calendar.HOUR_OF_DAY, 0);
		dayTwo.set(Calendar.MINUTE, 0);
		dayTwo.set(Calendar.SECOND, 0);
		dayTwo.set(Calendar.MILLISECOND, 0);
		return (int) TimeUnit.DAYS.convert((dayTwo.getTimeInMillis() - dayOne.getTimeInMillis()) , TimeUnit.MILLISECONDS);
	}
}
