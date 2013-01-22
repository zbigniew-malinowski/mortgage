package pl.zm.mortgage.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pl.zm.mortgage.calc.InputData;

import com.vaadin.data.Validator;

public class BudgetValidator implements Validator{

	private static final long serialVersionUID = 1L;

	private InputData inputData;
	
	private static final NumberFormat nf = new DecimalFormat("#,###");
	
	public BudgetValidator(InputData inputData) {
		this.inputData = inputData;
	}

	public void validate(Object value) throws InvalidValueException {
		Integer val;
		try {
			val = Integer.valueOf((String) value);
		} catch (NumberFormatException e) {
			return ;
		}
		if(!isValid(value))
			throw new InvalidValueException("Budżet nie może być niższy od ceny czynszu ani kosztu wynajmu" );
		
	}

	public boolean isValid(Object value) {
		Integer val;
		try {
			val = Integer.valueOf((String) value);
		} catch (NumberFormatException e) {
			return false;
		}
		return inputData.validateBudget(val);
	}

	
}
