package KedelidzeKrutyakov.conveyor.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanOfferDTO { // Возможное предложение по кредиту
    private Long applicationId; // Номер заявки
    private BigDecimal requestedAmount; // Запрашиваемая сумма кредита
    private BigDecimal totalAmount; // Общая сумма кредита
    private Integer term; // Срок кредита в месяцах
    private BigDecimal monthlyPayment; // Ежемесячный платеж
    private BigDecimal rate; // Процентная ставка по кредиту
    private Boolean InsuranceEnabled; // Включена ли страховка
    private Boolean SalaryClient; // Является ли клиент зарплатным (получает з.п. на счет в этом банке)
}