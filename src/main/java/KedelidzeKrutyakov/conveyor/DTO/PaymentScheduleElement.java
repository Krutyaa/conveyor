package KedelidzeKrutyakov.conveyor.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentScheduleElement { // График платежей
    private Integer number; // Номер платежа
    private LocalDate date; // Дата платежа
    private BigDecimal totalPayment; // Общая сумма платежа
    private BigDecimal interestPayment; // Платеж по процентам
    private BigDecimal debtPayment; // Платеж по основному долгу
    private BigDecimal remainingDebt; // Оставшаяся задолжность

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public void setInterestPayment(BigDecimal interestPayment) {
        this.interestPayment = interestPayment;
    }

    public void setDebtPayment(BigDecimal debtPayment) {
        this.debtPayment = debtPayment;
    }

    public void setRemainingDebt(BigDecimal remainingDebt) {
        this.remainingDebt = remainingDebt;
    }

    public Integer getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public BigDecimal getInterestPayment() {
        return interestPayment;
    }

    public BigDecimal getDebtPayment() {
        return debtPayment;
    }

    public BigDecimal getRemainingDebt() {
        return remainingDebt;
    }
}