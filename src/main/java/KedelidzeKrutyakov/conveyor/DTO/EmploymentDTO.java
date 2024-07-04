package KedelidzeKrutyakov.conveyor.DTO;

import java.math.BigDecimal;

public class EmploymentDTO { // Информация о занятости заявителя
    private EmploymentStatus employmentStatus; // Статус занятости
    private String employerINN; // ИНН работодателя
    private BigDecimal salary; // Зарплата
    private Position position; // Должность
    private Integer workExperienceTotal; // Стаж работы
    private Integer workExperienceCurrent; // Стаж работы на текущей работе

    public enum EmploymentStatus {
        WORKING,
        NOT_WORKING,
        SELF_EMPLOYED, // Самозанятый
        RETIRED // Пенсионер
    }

    public enum Position {
        MANAGER, // Менеджер
        ENGINEER, // Инженер
        ANALYST, // Аналитик
        CLERK // Секретарь
    }

    public void setEmployerINN(String employerINN) {
        this.employerINN = employerINN;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setWorkExperienceTotal(Integer workExperienceTotal) {
        this.workExperienceTotal = workExperienceTotal;
    }

    public void setWorkExperienceCurrent(Integer workExperienceCurrent) {
        this.workExperienceCurrent = workExperienceCurrent;
    }

    public String getEmployerINN() {
        return employerINN;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public Integer getWorkExperienceTotal() {
        return workExperienceTotal;
    }

    public Integer getWorkExperienceCurrent() {
        return workExperienceCurrent;
    }
}