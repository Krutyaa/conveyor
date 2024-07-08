package KedelidzeKrutyakov.conveyor.util;

import KedelidzeKrutyakov.conveyor.DTO.ScoringDataDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class RateCalculator {

    // Расчет ставки на основе данных о страховке и зарплатности клиента
    public static BigDecimal calculateRate(BigDecimal baseRate, boolean isInsuranceEnabled, boolean isSalaryClient) {
        // Условие страховки
        if (isInsuranceEnabled) {
            baseRate = baseRate.subtract(new BigDecimal("0.03"));
        }
        // Условие зарплатности клиента
        if (isSalaryClient) {
            baseRate = baseRate.subtract(new BigDecimal("0.01"));
        }
        return baseRate;
    }

    // Метод для расчета окончательной процентной ставки на основе данных скоринга и базовой ставки
    public static BigDecimal calculateFinallyRate(ScoringDataDTO scoringDataDTO, BigDecimal baseRate) {
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