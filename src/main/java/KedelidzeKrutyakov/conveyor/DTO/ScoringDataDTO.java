package KedelidzeKrutyakov.conveyor.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private Boolean isInsuranceEnabled; // Включена ли страховка
    private Boolean isSalaryClient; // Является ли клиент зарплатным (получает з.п. на счет в этом банке)

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum MaritalStatus {
        SINGLE,
        MARRIED,
        DIVORCED, // Разведен
        WIDOWED // Вдовец
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

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setPassportIssueDate(LocalDate passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public void setPassportIssueBranch(String passportIssueBranch) {
        this.passportIssueBranch = passportIssueBranch;
    }

    public void setEmployment(EmploymentDTO employment) {
        this.employment = employment;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setDependentAmount(Integer dependentAmount) {
        this.dependentAmount = dependentAmount;
    }

    public void setInsuranceEnabled(Boolean insuranceEnabled) {
        isInsuranceEnabled = insuranceEnabled;
    }

    public void setSalaryClient(Boolean salaryClient) {
        isSalaryClient = salaryClient;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public LocalDate getPassportIssueDate() {
        return passportIssueDate;
    }

    public String getPassportIssueBranch() {
        return passportIssueBranch;
    }

    public Integer getDependentAmount() {
        return dependentAmount;
    }

    public EmploymentDTO getEmployment() {
        return employment;
    }

    public String getAccount() {
        return account;
    }

    public Boolean getInsuranceEnabled() {
        return isInsuranceEnabled;
    }

    public Boolean getSalaryClient() {
        return isSalaryClient;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}