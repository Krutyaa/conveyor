package KedelidzeKrutyakov.conveyor.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentScheduleElement { // График платежей
    private Integer number; // Номер платежа
    private LocalDate date; // Дата платежа
    private BigDecimal totalPayment; // Общая сумма платежа
    private BigDecimal interestPayment; // Платеж по процентам
    private BigDecimal debtPayment; // Платеж по основному долгу
    private BigDecimal remainingDebt; // Оставшаяся задолжность
}