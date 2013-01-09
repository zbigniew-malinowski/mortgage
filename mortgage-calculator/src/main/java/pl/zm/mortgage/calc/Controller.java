package pl.zm.mortgage.calc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Controller implements Serializable {
    
	private static final long serialVersionUID = -1618425494587714741L;

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>>ChartData<T> calculate(InputData input, Class<T> type){
		Map<Class<? extends Enum<?>>, ChartData<?>> map = new HashMap<Class<? extends Enum<?>>, ChartData<?>>();
		
		ChartData<Money> money = new ChartData<Money>(Money.class);
		ChartData<Time> time = new ChartData<Time>(Time.class);
		map.put(Money.class, money);
		map.put(Time.class, time);
        
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
            
            money.addX(rentTime/12);
            money.setData(Money.DEPOSIT, Calculations.formatAmountKpln(depositValue));            
            money.setData(Money.CAPITAL, Calculations.formatAmountKpln(creditCapital));
            money.setData(Money.INTEREST, Calculations.formatAmountKpln(creditInterest));
            
            time.addX(rentTime/12);
            time.setData(Time.RENT, rentTime);
            time.setData(Time.CREDIT, installmentsCount);
        }
        
        return (ChartData<T>) map.get(type);
    }
	
	
	
}
