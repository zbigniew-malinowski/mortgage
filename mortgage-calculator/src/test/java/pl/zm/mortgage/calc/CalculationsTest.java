package pl.zm.mortgage.calc;

import org.junit.Assert;
import org.junit.Test;

public class CalculationsTest {

    @Test
    public void testInstallmentsCount() throws Exception {

        double annualInterestRate = 6;
        int capital = 100000;
        int installment = 2000;
        int installmentsCount = Calculations.calculateInstallmentsCount(capital, annualInterestRate, installment);
        Assert.assertEquals(57, installmentsCount);
    }

    @Test
    public void testDeposit() throws Exception {
        int ic = 12;
        int monthlyCommitment = 1000;
        double ir = 5;
        int depositValue = Calculations.calculateDepositValue(monthlyCommitment, ir, ic);
        Assert.assertEquals(12278, depositValue);
    }
}
