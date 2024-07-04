package KedelidzeKrutyakov.conveyor.DTO;

import java.math.BigDecimal;

public class LoanOfferDTO { // Возможное предложение по кредиту
    private Long applicationId; // Номер заявки
    private BigDecimal requestedAmount; // Запрашиваемая сумма кредита
    private BigDecimal totalAmount; // Общая сумма кредита
    private Integer term; // Срок кредита в месяцах
    private BigDecimal monthlyPayment; // Ежемесячный платеж
    private BigDecimal rate; // Процентная ставка по кредиту
    private Boolean isInsuranceEnabled; // Включена ли страховка
    private Boolean isSalaryClient; // Является ли клиент зарплатным (получает з.п. на счет в этом банке)

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setInsuranceEnabled(Boolean insuranceEnabled) {
        isInsuranceEnabled = insuranceEnabled;
    }

    public void setSalaryClient(Boolean salaryClient) {
        isSalaryClient = salaryClient;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public Integer getTerm() {
        return term;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Boolean getInsuranceEnabled() {
        return isInsuranceEnabled;
    }

    public Boolean getSalaryClient() {
        return isSalaryClient;
    }
}