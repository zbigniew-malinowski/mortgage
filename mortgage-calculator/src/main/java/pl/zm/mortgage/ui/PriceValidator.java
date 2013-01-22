package pl.zm.mortgage.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pl.zm.mortgage.calc.InputData;

import com.vaadin.data.Validator;

public class PriceValidator implements Validator{

	private static final long serialVersionUID = 1L;

	private InputData inputData;
	
	private static final NumberFormat nf = new DecimalFormat("#,###");
	
	public PriceValidator(InputData inputData) {
		this.inputData = inputData;
	}

	public void validate(Object value) throws InvalidValueException {
		Integer val;
		try {
			val = Integer.valueOf((String) value);
		} catch (NumberFormatException e) {
			return ;
		}
		int maxPrice = inputData.getMaxPrice()/1000;
		if(maxPrice == 0)
			throw new InvalidValueException("Zbyt mały budżet" );
		
		if(!isValid(value))
			throw new InvalidValueException("Maksymalna cena przy obecnych warunkach to " + maxPrice  +" 000 zł" );
		
	}

	public boolean isValid(Object value) {
		Integer val;
		try {
			val = Integer.valueOf((String) value);
		} catch (NumberFormatException e) {
			return false;
		}
		return inputData.validatePrice(val);
	}

	
}
