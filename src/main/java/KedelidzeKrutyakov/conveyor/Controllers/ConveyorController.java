package KedelidzeKrutyakov.conveyor.Controllers;

import KedelidzeKrutyakov.conveyor.DTO.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
public class ConveyorController {

    // Базовая ставка
    private static final BigDecimal BASE_RATE = new BigDecimal("0.10");

    // Стоимость страховки
    private static final BigDecimal INSURANCE_COST = new BigDecimal("100000");

    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequest) {
        List<LoanOfferDTO> loanOffers = new ArrayList<>();

        long nextApplicationId = 1;

        // Комбинации isInsuranceEnabled и isSalaryClient
        boolean[][] combinations = {
                {false, false},
                {false, true},
                {true, false},
                {true, true}
        };

        for (boolean[] combination : combinations) {
            boolean isInsuranceEnabled = combination[0];
            boolean isSalaryClient = combination[1];

            // Рассчитываем ставку и общую сумму кредита по текущим условиям
            BigDecimal rate = calculateRate(isInsuranceEnabled, isSalaryClient);
            BigDecimal totalAmount = calculateTotalAmount(loanApplicationRequest.getAmount(), isInsuranceEnabled);

            // Рассчитываем ежемесячный платеж по текущим условиям
            BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, loanApplicationRequest.getTerm());

            // Создаем объект LoanOfferDTO для текущей комбинации условий
            LoanOfferDTO loanOffer = new LoanOfferDTO();
            loanOffer.setApplicationId(nextApplicationId++);
            loanOffer.setRequestedAmount(loanApplicationRequest.getAmount());
            loanOffer.setTotalAmount(totalAmount);
            loanOffer.setTerm(loanApplicationRequest.getTerm());
            loanOffer.setMonthlyPayment(monthlyPayment);
            loanOffer.setRate(rate);
            loanOffer.setInsuranceEnabled(isInsuranceEnabled);
            loanOffer.setSalaryClient(isSalaryClient);

            loanOffers.add(loanOffer);
        }

        // Сортировка списка по убыванию ставки
        loanOffers.sort((o1, o2) -> o2.getRate().compareTo(o1.getRate()));

        return loanOffers;
    }

    // Расчет ставки на основе данных о страховке и зарплатности клиента
    private BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        BigDecimal rate = BASE_RATE;

        // Условие страховки
        if (isInsuranceEnabled) {
            rate = rate.subtract(new BigDecimal("0.03")); // Уменьшение ставки на 3%
        }

        // Условие зарплатности клиента
        if (isSalaryClient) {
            rate = rate.subtract(new BigDecimal("0.01")); // Уменьшение ставки на 1%
        }

        return rate;
    }

    // Расчет общей суммы кредита с учетом страховки
    private BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled) {
        if (isInsuranceEnabled) {
            return amount.add(INSURANCE_COST); // Добавление стоимости страховки к сумме кредита
        } else {
            return amount;
        }
    }

    // Расчет аннуитетного платежа
    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        // Вычисляем месячную процентную ставку
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 20, BigDecimal.ROUND_HALF_UP);

        // Вычисляем базовый ежемесячный платеж (сумма кредита, деленная на количество месяцев)
        BigDecimal baseMonthlyPayment = amount.divide(BigDecimal.valueOf(term), 20, BigDecimal.ROUND_HALF_UP);

        // Вычисляем ежемесячную процентную составляющую (сумма кредита, умноженная на месячную процентную ставку)
        BigDecimal monthlyInterest = amount.multiply(monthlyRate);

        // Итоговый ежемесячный платеж
        BigDecimal monthlyPayment = baseMonthlyPayment.add(monthlyInterest);

        return monthlyPayment.setScale(4, BigDecimal.ROUND_HALF_UP);
    }


    @PostMapping("/calculation")
    public CreditDTO getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO) {
        // Проверка на условие страховки и зарплатности клиента
        boolean isInsuranceEnabled = scoringDataDTO.getInsuranceEnabled();
        boolean isSalaryClient = scoringDataDTO.getSalaryClient();

        // Вычисление процентной ставки
        BigDecimal rate = calculateRate(isInsuranceEnabled, isSalaryClient);
        // Расчет ежемесячного платежа
        BigDecimal monthlyPayment = calculateMonthlyPayment(scoringDataDTO.getAmount(), rate, scoringDataDTO.getTerm());
        // Расчет полной стоисоти кредита
        BigDecimal psk = calculatePSK(scoringDataDTO.getAmount(), monthlyPayment, scoringDataDTO.getTerm(), rate, isInsuranceEnabled);
        // Расчет графика платежей
        List<PaymentScheduleElement> paymentSchedule = calculatePaymentSchedule(scoringDataDTO.getAmount(), rate, scoringDataDTO.getTerm(), psk);

        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setAmount(scoringDataDTO.getAmount());
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setRate(rate);
        creditDTO.setPsk(psk);
        creditDTO.setMonthlyPayment(monthlyPayment);
        creditDTO.setInsuranceEnabled(scoringDataDTO.getInsuranceEnabled());
        creditDTO.setSalaryClient(scoringDataDTO.getSalaryClient());
        creditDTO.setPaymentSchedule(paymentSchedule);

        return creditDTO;
    }

    // Полная стоимость кредита
    private BigDecimal calculatePSK(BigDecimal amount, BigDecimal monthlyPayment, int term, BigDecimal rate, boolean isInsuranceEnabled) {
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
            psk = psk.add(INSURANCE_COST);
        }

        return psk.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Рассчет графика платежей
    private List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal amount, BigDecimal rate, int term, BigDecimal psk) {
        List<PaymentScheduleElement> schedule = new ArrayList<>();
        // Ежемесячный платеж
        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, rate, term);

        // Платеж по основному долгу
        BigDecimal monthlyPrincipalPayment = amount.divide(BigDecimal.valueOf(term), 10, BigDecimal.ROUND_HALF_UP);
        // Платеж по процентам
        BigDecimal monthlyInterestPayment = psk.multiply(rate).divide(BigDecimal.valueOf(12), 10, BigDecimal.ROUND_HALF_UP);
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