package KedelidzeKrutyakov.conveyor.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreditDTO { // Полный расчёт параметров кредита
    private BigDecimal amount; // Сумма кредита
    private Integer term; // Срок кредита
    private BigDecimal monthlyPayment; // Ежемесячный платеж
    private BigDecimal rate; // Процентная ставка
    private BigDecimal psk; // Полная стоимость кредита
    private Boolean InsuranceEnabled; //Наличие страховки
    private Boolean SalaryClient; // Является ли клиент зарплатным (получает з.п. на счет в этом банке)
    private List<PaymentScheduleElement> paymentSchedule; // График платежей
}