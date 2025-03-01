package ru.yandex.practicum.exception;

import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoDeliveryFoundException(NoDeliveryFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, "Не найдена доставка для расчёта", ex);
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoOrderFoundException(NoOrderFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, "Не найден заказ", ex);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(InternalServerErrorException ex) {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", ex);
    }


    private ErrorResponse errorResponse(HttpStatus status, String userMessage, Throwable ex) {

        return new ErrorResponse(
                ex.getCause(),
                List.of(ex.getStackTrace()),
                status.name(),
                userMessage,
                ex.getMessage(),
                List.of(ex.getSuppressed()),
                ex.getLocalizedMessage()
        );
    }
}