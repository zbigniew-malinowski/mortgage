package pl.zm.mortgage.calc;




public class Controller {
    
    public ChartData<?>[] calculate(InputData input){
        ChartData<ValueSeries> values = new ChartData<ValueSeries>(ValueSeries.class);
        ChartData<TimeSeries> time = new ChartData<TimeSeries>(TimeSeries.class);
        
        int capital = input.getFlatPrice();
        double creditIr = input.getCreditInterestRate();
        double depositIr = input.getDepositInterestRate();
        int installment = input.getBudget() - input.getAdministrativeRent();
        int monthlyCommitment = input.getBudget() - input.getHiredFlatRent();
        
        for (int rentTime = 0; rentTime <= Calculations.MAX_RENT_TIME; rentTime+=10) {
            
            int installmentsCount = Calculations.calculateInstallmentsCount(capital, creditIr, installment);
            int creditInterest = Calculations.calculateCreditInterest(capital, installment, installmentsCount);
            int depositValue = Calculations.calculateDepositValue(monthlyCommitment, depositIr, rentTime);
            int creditCapital = capital - depositValue;
            
            values.addX(rentTime);
            values.setData(ValueSeries.DEPOSIT, depositValue);            
            values.setData(ValueSeries.CAPITAL, creditCapital);
            values.setData(ValueSeries.INTEREST, creditInterest);
            
            time.addX(rentTime);
            time.setData(TimeSeries.RENT, rentTime);
            time.setData(TimeSeries.CREDIT, installmentsCount);
        }
        
        return new ChartData<?>[]{values, time};
    }
}
