package KedelidzeKrutyakov.conveyor.Service;

import KedelidzeKrutyakov.conveyor.DTO.*;
import KedelidzeKrutyakov.conveyor.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    // Базовая ставка
    private static final BigDecimal BASE_RATE = new BigDecimal("0.15");

    // Стоимость страховки
    private static final BigDecimal INSURANCE_COST = new BigDecimal("100000");

    // Метод для расчета 4 предложений
    public List<LoanOfferDTO> generateLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequest) {
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

            // Рассчитываем ставку по текущим условиям
            BigDecimal rate = RateCalculator.calculateRate(BASE_RATE, isInsuranceEnabled, isSalaryClient);
            // Рассчитываем общую сумму кредита без умножения страховки на процентную ставку
            BigDecimal totalAmount = PaymentCalculator.calculateTotalAmount(loanApplicationRequest.getAmount(), isInsuranceEnabled, INSURANCE_COST);
            // Рассчитываем ежемесячный платеж по текущим условиям
            BigDecimal monthlyPayment = PaymentCalculator.calculateMonthlyPayment(totalAmount, rate, loanApplicationRequest.getTerm(), isInsuranceEnabled, INSURANCE_COST);

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

    // Метод для расчета кредита
    public CreditDTO calculateCredit(ScoringDataDTO scoringData) {
        // Проверка на условие страховки и зарплатности клиента
        boolean isInsuranceEnabled = scoringData.getInsuranceEnabled();
        boolean isSalaryClient = scoringData.getSalaryClient();

        /// Вычисление базовой процентной ставки
        BigDecimal baseRate = RateCalculator.calculateRate(BASE_RATE, isInsuranceEnabled, isSalaryClient);
        // Вычисление окончательной процентной ставки на основе дополнительных условий
        BigDecimal rate = RateCalculator.calculateFinallyRate(scoringData, baseRate);
        // Рассчитываем общую сумму кредита без умножения страховки на процентную ставку
        BigDecimal totalAmount = PaymentCalculator.calculateTotalAmount(scoringData.getAmount(), isInsuranceEnabled, INSURANCE_COST);
        // Расчет ежемесячного платежа
        BigDecimal monthlyPayment = PaymentCalculator.calculateMonthlyPayment(totalAmount, rate, scoringData.getTerm(), isInsuranceEnabled, INSURANCE_COST);
        // Расчет полной стоимости кредита
        BigDecimal psk = PaymentCalculator.calculatePSK(scoringData.getAmount(), monthlyPayment, scoringData.getTerm(), rate, isInsuranceEnabled, INSURANCE_COST);
        // Расчет графика платежей
        List<PaymentScheduleElement> paymentSchedule = PaymentCalculator.calculatePaymentSchedule(scoringData.getAmount(), rate, scoringData.getTerm(), psk, isInsuranceEnabled, INSURANCE_COST);
        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setAmount(scoringData.getAmount());
        creditDTO.setTerm(scoringData.getTerm());
        creditDTO.setRate(rate);
        creditDTO.setPsk(psk);
        creditDTO.setMonthlyPayment(monthlyPayment);
        creditDTO.setInsuranceEnabled(scoringData.getInsuranceEnabled());
        creditDTO.setSalaryClient(scoringData.getSalaryClient());
        creditDTO.setPaymentSchedule(paymentSchedule);

        return creditDTO;
    }
}