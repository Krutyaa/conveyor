package KedelidzeKrutyakov.conveyor.Service.validation;

import KedelidzeKrutyakov.conveyor.DTO.LoanApplicationRequestDTO;
import KedelidzeKrutyakov.conveyor.DTO.ScoringDataDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
public class ValidationService {

    public void validateLoanApplicationRequest(LoanApplicationRequestDTO request) {
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