package KedelidzeKrutyakov.conveyor.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanApplicationRequestDTO { // Запрос на кредит
    private BigDecimal amount; //Сумма кредита
    private Integer term; // Срок кредита в месяцах
    private String firstName; // Имя
    private String lastName; // Фамилия
    private String middleName; // Отчество
    private String email; // Email пользователя
    private LocalDate birthdate; // День рождения
    private String passportSeries; // Серия паспорта
    private String passportNumber; // Номер паспорта
}