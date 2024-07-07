package KedelidzeKrutyakov.conveyor.Controllers;

import KedelidzeKrutyakov.conveyor.DTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
public class ConveyorController {

    // Базовая ставка
    private static final BigDecimal BASE_RATE = new BigDecimal("0.15");

    // Стоимость страховки
    private static final BigDecimal INSURANCE_COST = new BigDecimal("100000");

    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequest) {
        validateLoanApplicationRequest(loanApplicationRequest);
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
            BigDecimal rate = calculateRate(isInsuranceEnabled, isSalaryClient);
            // Рассчитываем общую сумму кредита без умножения страховки на процентную ставку
            BigDecimal totalAmount = calculateTotalAmount(loanApplicationRequest.getAmount(), isInsuranceEnabled);
            // Рассчитываем ежемесячный платеж по текущим условиям
            BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, loanApplicationRequest.getTerm(), isInsuranceEnabled);

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
        // Если страховка включена, добавляем её стоимость к общей сумме
        if (isInsuranceEnabled) {
            return amount.add(INSURANCE_COST);
        }
        return amount;
    }

    // Расчет аннуитетного платежа
    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term, boolean isInsuranceEnabled) {
        // Вычисляем месячную процентную ставку
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 20, BigDecimal.ROUND_HALF_UP);

        // Если страховка включена, выделяем её стоимость
        BigDecimal insuranceCost = isInsuranceEnabled ? INSURANCE_COST : BigDecimal.ZERO;
        BigDecimal loanWithoutInsurance = amount.subtract(insuranceCost);

        // Вычисляем базовый ежемесячный платеж (сумма кредита, деленная на количество месяцев)
        BigDecimal baseMonthlyPayment = loanWithoutInsurance.divide(BigDecimal.valueOf(term), 20, BigDecimal.ROUND_HALF_UP);

        // Вычисляем ежемесячную процентную составляющую (сумма кредита без страховки, умноженная на месячную процентную ставку)
        BigDecimal monthlyInterest = loanWithoutInsurance.multiply(monthlyRate);

        // Итоговый ежемесячный платеж (сумма основного платежа, процентной составляющей и страховки, распределенной на весь срок)
        BigDecimal monthlyPayment = baseMonthlyPayment.add(monthlyInterest).add(insuranceCost.divide(BigDecimal.valueOf(term), 20, BigDecimal.ROUND_HALF_UP));

        return monthlyPayment.setScale(4, BigDecimal.ROUND_HALF_UP);
    }


    @PostMapping("/calculation")
    public CreditDTO getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO) {
        // Проверка на условие страховки и зарплатности клиента
        boolean isInsuranceEnabled = scoringDataDTO.getInsuranceEnabled();
        boolean isSalaryClient = scoringDataDTO.getSalaryClient();

        /// Вычисление базовой процентной ставки
        BigDecimal baseRate = calculateRate(isInsuranceEnabled, isSalaryClient);
        // Вычисление окончательной процентной ставки на основе дополнительных условий
        BigDecimal rate = calculateFinallyRate(scoringDataDTO, baseRate);
        // Рассчитываем общую сумму кредита без умножения страховки на процентную ставку
        BigDecimal totalAmount = calculateTotalAmount(scoringDataDTO.getAmount(), isInsuranceEnabled);
        // Расчет ежемесячного платежа
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, scoringDataDTO.getTerm(), isInsuranceEnabled);
        // Расчет полной стоисоти кредита
        BigDecimal psk = calculatePSK(scoringDataDTO.getAmount(), monthlyPayment, scoringDataDTO.getTerm(), rate, isInsuranceEnabled);
        // Расчет графика платежей
        List<PaymentScheduleElement> paymentSchedule = calculatePaymentSchedule(scoringDataDTO.getAmount(), rate, scoringDataDTO.getTerm(), psk, isInsuranceEnabled);

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
    private List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal amount, BigDecimal rate, int term, BigDecimal psk, boolean isInsuranceEnabled) {
        List<PaymentScheduleElement> schedule = new ArrayList<>();
        // Рассчитываем общую сумму кредита без умножения страховки на процентную ставку
        BigDecimal totalAmount = calculateTotalAmount(amount, isInsuranceEnabled);
        // Ежемесячный платеж
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, term, isInsuranceEnabled);

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

    // Валидаиця данных
    private void validateLoanApplicationRequest(LoanApplicationRequestDTO request) {
        // Валидация имени, фамилии и отчества
        if (!isValidName(request.getFirstName()) || !isValidName(request.getLastName()) ||
                (!isValidName(request.getMiddleName()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Имя, фамилия и отчество должны содержать от 2 до 30 латинских букв.");
        }

        // Валидация суммы кредита
        if (request.getAmount().compareTo(BigDecimal.valueOf(10000)) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сумма кредита должна быть больше или равна 10000.");
        }

        // Валидация срока кредита
        if (request.getTerm() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Срок кредита должен быть больше или равен 6 месяцам.");
        }

        // Валидация даты рождения
        if (!isValidBirthDate(request.getBirthdate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата рождения должна быть не позднее 18 лет с текущего дня.");
        }

        // Валидация Email
        if (!isValidEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный Email адрес.");
        }

        // Валидация серии и номера паспорта
        if (!isValidPassportSeries(request.getPassportSeries()) || !isValidPassportNumber(request.getPassportNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Серия паспорта должна содержать 4 цифры, номер паспорта должен содержать 6 цифр.");
        }
    }

    private boolean isValidName(String name) {
        return name != null && name.matches("[A-Za-z]{2,30}");
    }

    private boolean isValidBirthDate(LocalDate birthDate) {
        return birthDate != null && Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("[\\w\\.]{2,50}@[\\w\\.]{2,20}");
    }

    private boolean isValidPassportSeries(String passportSeries) {
        return passportSeries != null && passportSeries.matches("\\d{4}");
    }

    private boolean isValidPassportNumber(String passportNumber) {
        return passportNumber != null && passportNumber.matches("\\d{6}");
    }


    // Метод для расчета окончательной процентной ставки на основе данных скоринга и базовой ставки
    private BigDecimal calculateFinallyRate(ScoringDataDTO scoringDataDTO, BigDecimal baseRate) {
        BigDecimal rate = baseRate;

        // Рабочий статус
        switch (scoringDataDTO.getEmployment().getEmploymentStatus()) {
            case UNEMPLOYED:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вы безработны. Отклонено");
            case SELF_EMPLOYED:
                rate = rate.add(new BigDecimal("0.01")); // Увеличение ставки на 1% для самозанятых
                break;
            case BUSINESS_OWNER:
                rate = rate.add(new BigDecimal("0.03")); // Увеличение ставки на 3% для владельцев бизнеса
                break;
        }

        // Позиция на работе
        switch (scoringDataDTO.getEmployment().getPosition()) {
            case MID_MANAGER:
                rate = rate.subtract(new BigDecimal("0.02")); // Уменьшение ставки на 2% для менеджеров среднего звена
                break;
            case TOP_MANAGER:
                rate = rate.subtract(new BigDecimal("0.04")); // Уменьшение ставки на 4% для топ-менеджеров
                break;
        }

        // Зарплата меньше 20 суммы займа
        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmployment().getSalary().multiply(new BigDecimal("20"))) > 0) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сумма кредита больше, чем 20 зарплат. Отклонено");
        }

        // Семейное положение
        if (scoringDataDTO.getMaritalStatus() == ScoringDataDTO.MaritalStatus.MARRIED) {
            rate = rate.subtract(new BigDecimal("0.03")); // Уменьшение ставки на 3% для замужем/женатых
        } else if (scoringDataDTO.getMaritalStatus() == ScoringDataDTO.MaritalStatus.SINGLE) {
            rate = rate.add(new BigDecimal("0.01")); // Увеличение ставки на 1% для разведенных/одиноких
        }

        // Количество иждивенцев
        if (scoringDataDTO.getDependentAmount() > 1) {
            rate = rate.add(new BigDecimal("0.01")); // Увеличение ставки на 1% для количества иждивенцев больше 1
        }

        // Возраст
        int age = Period.between(scoringDataDTO.getBirthdate(), LocalDate.now()).getYears();
        if (age < 20 || age > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вам должно быть от 20 до 60 лет. Отклонено");
        }

        // Пол и возраст
        switch (scoringDataDTO.getGender()) {
            case FEMALE:
                if (age >= 35 && age <= 60) {
                    rate = rate.subtract(new BigDecimal("0.03")); // Уменьшение ставки на 3% для женщин от 35 до 60 лет
                }
                break;
            case MALE:
                if (age >= 30 && age <= 55) {
                    rate = rate.subtract(new BigDecimal("0.03")); // Уменьшение ставки на 3% для мужчин от 30 до 55 лет
                }
                break;
            case NON_BINARY:
                rate = rate.add(new BigDecimal("0.03")); // Увеличение ставки на 3% для дурачков
                break;
        }

        // Стаж работы
        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12 || scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ваш стаж работы мал. Отклонено");
        }
        return rate;
    }
}