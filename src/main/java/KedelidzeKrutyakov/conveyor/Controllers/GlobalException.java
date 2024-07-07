package KedelidzeKrutyakov.conveyor.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalException {

    // Обработчик исключений типа ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        // Получаем HTTP статус из исключения ResponseStatusException
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        // Создаем объект ErrorResponse для возврата клиенту
        ErrorResponse errorResponse = new ErrorResponse(status, ex.getReason());

        // Возвращаем ResponseEntity с объектом ErrorResponse и HTTP статусом
        return new ResponseEntity<>(errorResponse, status);
    }

    // Обработчик всех остальных исключений типа Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        // Создаем объект ErrorResponse для возврата клиенту
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка.");

        // Возвращаем ResponseEntity с объектом ErrorResponse и HTTP статусом 500 (INTERNAL_SERVER_ERROR)
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
