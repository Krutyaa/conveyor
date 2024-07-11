package KedelidzeKrutyakov.conveyor.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ScoringDataDTO { // Данные заявителя для скоринга
    private BigDecimal amount; // Сумма кредита
    private Integer term; // Срок кредита в месяцах
    private String firstName; // Имя
    private String lastName; // Фамилия
    private String middleName; // Отчество
    private Gender gender; // Пол // Пол
    private LocalDate birthdate; // День рождения
    private String passportSeries; // Серия паспорта
    private String passportNumber; // Номер паспорта
    private LocalDate passportIssueDate; // Дата выдачи паспорта
    private String passportIssueBranch; // Орган выдачи паспорта
    private MaritalStatus maritalStatus; // Семейное положение
    private Integer dependentAmount; // Количество иждивенцев (дети)
    private EmploymentDTO employment; // Информация о занятности заявителя
    private String account; // Номер банковского счета
    private Boolean InsuranceEnabled; // Включена ли страховка
    private Boolean SalaryClient; // Является ли клиент зарплатным (получает з.п. на счет в этом банке)

    public enum Gender {
        MALE,
        FEMALE,
        NON_BINARY // Не бинарный
    }

    public enum MaritalStatus {
        SINGLE, // Холост/Не замужем
        MARRIED // Замужем/Женат
    }
}