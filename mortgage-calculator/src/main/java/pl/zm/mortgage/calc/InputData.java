package pl.zm.mortgage.calc;



public class InputData {

    private Integer budget = 3000;
    private Double creditInterestRate = 6.0;
    private Double depositInterestRate = 5.0;
    private Integer flatPrice = 300000;
    private Double flatPriceDynamic = 0.0;
    private Integer administrativeRent = 500;
    private Integer hiredFlatRent = 1000;
    
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
    public Double getFlatPriceDynamic() {
        return flatPriceDynamic;
    }
    public void setFlatPriceDynamic(Double flatPriceDynamic) {
        this.flatPriceDynamic = flatPriceDynamic;
    }
    public Integer getAdministrativeRent() {
        return administrativeRent;
    }
    public void setAdministrativeRent(Integer administrativeRent) {
        this.administrativeRent = administrativeRent;
    }
    public Integer getHiredFlatRent() {
        return hiredFlatRent;
    }
    public void setHiredFlatRent(Integer hiredFlatRent) {
        this.hiredFlatRent = hiredFlatRent;
    }
    
}
