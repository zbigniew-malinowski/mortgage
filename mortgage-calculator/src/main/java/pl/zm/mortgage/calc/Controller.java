package pl.zm.mortgage.calc;

import java.io.Serializable;




public class Controller implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1618425494587714741L;

	public ChartData<?>[] calculate(InputData input){
        ChartData<ValueSeries> values = new ChartData<ValueSeries>(ValueSeries.class);
        ChartData<TimeSeries> time = new ChartData<TimeSeries>(TimeSeries.class);
        
        int capital = input.getFlatPrice();
        double creditIr = input.getCreditInterestRate();
        double depositIr = input.getDepositInterestRate();
        int installment = input.getBudget() - input.getAdministrativeRent();
        int monthlyCommitment = input.getBudget() - input.getHiredFlatRent();
        
        for (int rentTime = 0; rentTime <= Calculations.MAX_RENT_TIME; rentTime+=12) {
            
        	int depositValue = Calculations.calculateDepositValue(monthlyCommitment, depositIr, rentTime);
            int creditCapital = Math.max(capital - depositValue, 0);
            int installmentsCount = Calculations.calculateInstallmentsCount(creditCapital, creditIr, installment);
            int creditInterest = Calculations.calculateCreditInterest(creditCapital, installment, installmentsCount);
            
            values.addX(rentTime/12);
            values.setData(ValueSeries.DEPOSIT, Calculations.formatAmountKpln(depositValue));            
            values.setData(ValueSeries.CAPITAL, Calculations.formatAmountKpln(creditCapital));
            values.setData(ValueSeries.INTEREST, Calculations.formatAmountKpln(creditInterest));
            
            time.addX(rentTime/12);
            time.setData(TimeSeries.RENT, rentTime);
            time.setData(TimeSeries.CREDIT, installmentsCount);
        }
        
        return new ChartData<?>[]{values, time};
    }
}
