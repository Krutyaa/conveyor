package KedelidzeKrutyakov.conveyor.DTO;

import java.math.BigDecimal;

public class EmploymentDTO { // Информация о занятости заявителя
    private EmploymentStatus employmentStatus; // Статус занятости
    private String employerINN; // ИНН работодателя
    private BigDecimal salary; // Зарплата
    private Position position; // Должность

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public Position getPosition() {
        return position;
    }

    private Integer workExperienceTotal; // Стаж работы
    private Integer workExperienceCurrent; // Стаж работы на текущей работе

    public enum EmploymentStatus {
        UNEMPLOYED, // безработный
        SELF_EMPLOYED, // самозанятый
        EMPLOYED, // трудоустроен
        BUSINESS_OWNER // владелец бизнеса
    }

    public enum Position {
        WORKER, // работник
        MID_MANAGER, // менеджер среднего звена
        TOP_MANAGER,// топ-менеджер
        OWNER, // владелец
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