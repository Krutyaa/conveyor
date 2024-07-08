package KedelidzeKrutyakov.conveyor.util;

import KedelidzeKrutyakov.conveyor.DTO.PaymentScheduleElement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentCalculator {

    // Расчет общей суммы кредита с учетом страховки
    public static BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled, BigDecimal insuranceCost) {
        // Если страховка включена, добавляем её к общей стоимости суммы
        if (isInsuranceEnabled) {
            return amount.add(insuranceCost);
        }
        return amount;
    }

    // Расчет аннуитетного платежа
    public static BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term, boolean isInsuranceEnabled, BigDecimal insuranceCost) {
        // Вычисляем месячную процентную ставку
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 20, BigDecimal.ROUND_HALF_UP);

        // Если страховка включена, выделяем её стоимость
        BigDecimal insuranceCostValue = isInsuranceEnabled ? insuranceCost : BigDecimal.ZERO;
        BigDecimal loanWithoutInsurance = amount.subtract(insuranceCostValue);

        // Вычисляем базовый ежемесячный платеж (сумма кредита, деленная на количество месяцев)
        BigDecimal baseMonthlyPayment = loanWithoutInsurance.divide(BigDecimal.valueOf(term), 20, BigDecimal.ROUND_HALF_UP);

        // Вычисляем ежемесячную процентную составляющую (сумма кредита без страховки, умноженная на месячную процентную ставку)
        BigDecimal monthlyInterest = loanWithoutInsurance.multiply(monthlyRate);

        // Итоговый ежемесячный платеж (сумма основного платежа, процентной составляющей и страховки, распределенной на весь срок)
        BigDecimal monthlyPayment = baseMonthlyPayment.add(monthlyInterest).add(insuranceCostValue.divide(BigDecimal.valueOf(term), 20, BigDecimal.ROUND_HALF_UP));

        return monthlyPayment.setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    // Полная стоимость кредита
    public static BigDecimal calculatePSK(BigDecimal amount, BigDecimal monthlyPayment, int term, BigDecimal rate, boolean isInsuranceEnabled, BigDecimal insuranceCost) {
        // Вычисляем общую процентную ставку за весь срок кредита
        BigDecimal totalRate = rate.multiply(BigDecimal.valueOf(term));

        // Вычисляем месячную процентную ставку
        BigDecimal monthlyRate = totalRate.divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP);

        // Вычисляем общие начисленные проценты за весь срок кредита
        BigDecimal totalInterest = monthlyRate.multiply(amount);

        // Полная стоимость кредита без страховки
        BigDecimal psk = amount.add(totalInterest);

        // Если страховка включена, добавляем стоимость страховки
        if (isInsuranceEnabled) {
            psk = psk.add(insuranceCost);
        }

        return psk.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Рассчет графика платежей
    public static List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal amount, BigDecimal rate, int term, BigDecimal psk, boolean isInsuranceEnabled, BigDecimal insuranceCost) {
        List<PaymentScheduleElement> schedule = new ArrayList<>();
        // Рассчитываем общую сумму кредита без умножения страховки на процентную ставку
        BigDecimal totalAmount = calculateTotalAmount(amount, isInsuranceEnabled, insuranceCost);
        // Ежемесячный платеж
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, term, isInsuranceEnabled, insuranceCost);

        // Платеж по основному долгу
        BigDecimal monthlyPrincipalPayment = totalAmount.divide(BigDecimal.valueOf(term), 10, BigDecimal.ROUND_HALF_UP);
        // Платеж по процентам
        BigDecimal monthlyInterestPayment = amount.multiply(rate).divide(BigDecimal.valueOf(12), 10, BigDecimal.ROUND_HALF_UP);
        // Оставшийся платеж
        BigDecimal remainingPrincipal = psk;

        for (int i = 0; i < term; i++) {
            remainingPrincipal = remainingPrincipal.subtract(monthlyPayment);

            PaymentScheduleElement element = new PaymentScheduleElement();
            element.setNumber(i + 1);
            element.setDate(LocalDate.now().plusMonths(i + 1));
            element.setTotalPayment(monthlyPayment);
            element.setInterestPayment(monthlyInterestPayment);
            element.setDebtPayment(monthlyPrincipalPayment);
            element.setRemainingDebt(remainingPrincipal);

            schedule.add(element);
        }
        return schedule;
    }
}