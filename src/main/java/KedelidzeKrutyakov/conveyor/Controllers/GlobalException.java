package KedelidzeKrutyakov.conveyor.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalException {

    // Обработчик исключений типа ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        // Выводим трассировку исключения в консоль
        ex.printStackTrace();

        // Получаем HTTP статус из исключения ResponseStatusException
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        // Создаем объект ErrorResponse для возврата клиенту
        ErrorResponse errorResponse = new ErrorResponse(status, ex.getReason());

        // Возвращаем ResponseEntity с объектом ErrorResponse и HTTP статусом
        return new ResponseEntity<>(errorResponse, status);
    }

    // Обработчик исключений типа HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ex.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Некорректный формат данных: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Обработчик исключений типа NoResourceFoundException
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        ex.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, "Ресурс не найден: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Обработчик всех остальных исключений типа Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Вложенный класс для представления ошибки (ErrorResponse)
    static class ErrorResponse {
        private HttpStatus status;
        private String message;

        // Конструктор для создания объекта ErrorResponse
        public ErrorResponse(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        // Геттеры для получения статуса и сообщения ошибки
        public HttpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}