package com.sample.assignment.web;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sample.assignment.model.ClassificationRequest;
import com.sample.assignment.model.ClassificationResponse;
import com.sample.assignment.service.CustomerClassificationService;

@Controller
public class CustomerClassificationController {
	@Autowired private CustomerClassificationService classificationService;
	
	@RequestMapping(value="/classifyForm", method = RequestMethod.GET)
    public ModelAndView showClassifyForm(ModelMap model){
		model.addAttribute("customerIds", classificationService.getCustomerIds());
		model.addAttribute("months", getMonths());
		model.addAttribute("years", getYears());
		return new ModelAndView("classifyForm", "command", new ClassificationRequest());
    }
	
	@RequestMapping(value="/classifyResult", method = RequestMethod.POST)
	public String showClassifyResult(@ModelAttribute("SpringWeb")ClassificationRequest classificationRequest, ModelMap model) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.MONTH, classificationRequest.getMonth());
		date.set(Calendar.YEAR, classificationRequest.getYear());
		
		ClassificationResponse result = classificationService.classifyCustomer(classificationRequest.getId(), getStartDate(date), getEndDate(date));
		model.addAttribute("classifications", result.getClassifications());
	    model.addAttribute("balance", result.getBalance());
	    model.addAttribute("transactions", result.getTransactions());
	      
		return "classifyResult";
	}
	
	private Calendar getStartDate(Calendar actualDate) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.MONTH, actualDate.get(Calendar.MONTH));
		date.set(Calendar.YEAR, actualDate.get(Calendar.YEAR));
		date.set(Calendar.DATE, actualDate.getActualMinimum(Calendar.DAY_OF_MONTH));
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date;
	}
	
	private Calendar getEndDate(Calendar actualDate) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.MONTH, actualDate.get(Calendar.MONTH));
		date.set(Calendar.YEAR, actualDate.get(Calendar.YEAR));
		date.set(Calendar.DATE, actualDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date;
	}
	
	private Map<Integer,String> getMonths() {
		Map<Integer,String> months = new LinkedHashMap<>();
		months.put(1, "January");
		months.put(2, "February");
		months.put(3, "March");
		months.put(4, "April");
		months.put(5, "May");
		months.put(6, "June");
		months.put(7, "July");
		months.put(8, "August");
		months.put(9, "September");
		months.put(10, "October");
		months.put(11, "November");
		months.put(12, "December");
		return months;
	}
	
	private Map<Integer,String> getYears() {
		Map<Integer,String> years = new LinkedHashMap<>();
		years.put(2016, "2016");
		return years;
	}
}
