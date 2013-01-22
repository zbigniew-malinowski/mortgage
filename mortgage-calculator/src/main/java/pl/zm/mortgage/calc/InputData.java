package pl.zm.mortgage.calc;

import java.io.Serializable;

public class InputData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1421035799272219368L;

	private Integer budget = 3000;
	private Double creditInterestRate = 6.0;
	private Double depositInterestRate = 5.0;
	private Integer flatPrice = 300000;
	private Integer savings = 0;
	private Integer flatCost = 500;
	private Integer flatRent = 1000;

	public Integer getBudget() {
		return budget;
	}

	public void setBudget(Integer budget) {
		this.budget = budget;
	}

	public Double getCreditInterestRate() {
		return creditInterestRate;
	}

	public void setCreditInterestRate(Double creditInterestRate) {
		this.creditInterestRate = creditInterestRate;
	}

	public Double getDepositInterestRate() {
		return depositInterestRate;
	}

	public void setDepositInterestRate(Double depositInterestRate) {
		this.depositInterestRate = depositInterestRate;
	}

	public Integer getFlatPrice() {
		return flatPrice;
	}

	public void setFlatPrice(Integer flatPrice) {
		this.flatPrice = flatPrice;
	}

	public Integer getSavings() {
		return savings;
	}

	public void setSavings(Integer savings) {
		this.savings = savings;
	}

	public Integer getFlatCost() {
		return flatCost;
	}

	public void setFlatCost(Integer flatCost) {
		this.flatCost = flatCost;
	}

	public Integer getFlatRent() {
		return flatRent;
	}

	public void setFlatRent(Integer flatRent) {
		this.flatRent = flatRent;
	}

	public boolean validateBudget(Integer budget) {
		if (budget <= flatRent || budget <= flatCost)
			return false;
		return true;

	}

	public int getMaxPrice(Integer budget) {
		return Math.max(Calculations.calculateCapital(creditInterestRate, budget - flatRent, Calculations.MAX_INSTALLMENTS_COUNT) + savings, 0);
	}

	public int getMaxPrice() {
		return getMaxPrice(budget);
	}

	public boolean validatePrice(Integer val) {
		return getMaxPrice() > val;
	}
}
