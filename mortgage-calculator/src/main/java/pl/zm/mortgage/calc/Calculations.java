package pl.zm.mortgage.calc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Calculations {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWELVE = new BigDecimal("12");
    
    public static final int MAX_INSTALLMENTS_COUNT = 360;
    public static final int MAX_RENT_TIME = 120;
    

    public static int calculateInstallmentsCount(int capital, double annualInterestRate, int installment) {

    	if(capital <= 0)
    		return 0;
    	
        for (int installmentNr = 1; installmentNr <= MAX_INSTALLMENTS_COUNT; installmentNr++) {
            int interest = calculateInterest(capital, annualInterestRate);
            if (installment <= interest)
                throw new IllegalStateException("Incorrect installment");
            capital = capital - (installment - interest);
            if (capital <= 0)
                return installmentNr;
        }
        throw new IllegalStateException("Incorrect capital");
    }

    public static int calculateCreditInterest(int capital, int installment, int installmentsCount) {
    	if(capital <= 0)
    		return 0;
        BigDecimal ic = BigDecimal.valueOf(installmentsCount);
        BigDecimal c = BigDecimal.valueOf(capital);
        return BigDecimal.valueOf(installment).multiply(ic).subtract(c).intValue();

    }

    /**
     * Odsetki miesieczne.
     * 
     * @param capital
     * @param annualInterestRate
     * @return
     */
    private static int calculateInterest(int capital, double annualInterestRate) {
    	if(capital <= 0)
    		return 0;
        BigDecimal ir = new BigDecimal("" + annualInterestRate).divide(HUNDRED, MathContext.DECIMAL64);
        ir = ir.divide(TWELVE, MathContext.DECIMAL64);
        return BigDecimal.valueOf(capital).multiply(ir).intValue();
    }

    public static int calculateCapital(double annualInterestRate, int installment, int installmentsCount) {
    	BigDecimal ir = new BigDecimal("" + annualInterestRate).divide(HUNDRED, MathContext.DECIMAL64);
        ir = ir.divide(TWELVE, MathContext.DECIMAL64);
        BigDecimal pow = BigDecimal.ONE.add(ir).pow(installmentsCount);
        return BigDecimal.valueOf(installment).multiply(pow.subtract(BigDecimal.ONE)).divide(ir.multiply(pow), MathContext.DECIMAL64).intValue();
    }

    public static int calculateDepositValue(int initialSavings, int monthlyCommitment, double annualInterestRate, int installmentsCount) {
        BigDecimal ir = new BigDecimal("" + annualInterestRate).divide(HUNDRED, MathContext.DECIMAL64);
        ir = ir.divide(TWELVE, MathContext.DECIMAL64);
        BigDecimal q = ir.add(BigDecimal.ONE);
        BigDecimal factor1 = BigDecimal.ONE.subtract(q.pow(installmentsCount)).divide(BigDecimal.ONE.subtract(q), RoundingMode.HALF_UP);
        int savingsValue = BigDecimal.valueOf(initialSavings).multiply(q.pow(installmentsCount)).intValue();
        return factor1.multiply(BigDecimal.valueOf(monthlyCommitment)).intValue() + savingsValue;
    }
    
    public static int formatAmountKpln(int number) {
		return (int) Math.round(number /1000.0); 
	}
}
