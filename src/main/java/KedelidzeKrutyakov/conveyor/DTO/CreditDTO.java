package KedelidzeKrutyakov.conveyor.DTO;

import java.math.BigDecimal;
import java.util.List;

public class CreditDTO { // Полный расчёт параметров кредита
    private BigDecimal amount; // Сумма кредита
    private Integer term; // Срок кредита
    private BigDecimal monthlyPayment; // Ежемесячный платеж
    private BigDecimal rate; // Процентная ставка
    private BigDecimal psk; // Полная стоимость кредита
    private Boolean isInsuranceEnabled; //Наличие страховки
    private Boolean isSalaryClient; // Является ли клиент зарплатным (получает з.п. на счет в этом банке)
    private List<PaymentScheduleElement> paymentSchedule; // График платежей

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setPsk(BigDecimal psk) {
        this.psk = psk;
    }

    public void setInsuranceEnabled(Boolean insuranceEnabled) {
        isInsuranceEnabled = insuranceEnabled;
    }

    public void setSalaryClient(Boolean salaryClient) {
        isSalaryClient = salaryClient;
    }

    public void setPaymentSchedule(List<PaymentScheduleElement> paymentSchedule) {
        this.paymentSchedule = paymentSchedule;
    }

    public List<PaymentScheduleElement> getPaymentSchedule() {
        return paymentSchedule;
    }

    public BigDecimal getAmount() {
        return amount;
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

    public BigDecimal getPsk() {
        return psk;
    }

    public Boolean getInsuranceEnabled() {
        return isInsuranceEnabled;
    }

    public Boolean getSalaryClient() {
        return isSalaryClient;
    }
}