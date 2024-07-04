package KedelidzeKrutyakov.conveyor.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTerm() {
        return term;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }
}