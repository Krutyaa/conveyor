package KedelidzeKrutyakov.conveyor.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmploymentDTO { // Информация о занятости заявителя
    private EmploymentStatus employmentStatus; // Статус занятости
    private String employerINN; // ИНН работодателя
    private BigDecimal salary; // Зарплата
    private Position position; // Должность
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
}