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

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAuthorizedUserException(NoProductsInShoppingCartException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, "Корзина пуста", ex);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorizedUserException(NotAuthorizedUserException ex) {
        return errorResponse(HttpStatus.UNAUTHORIZED, "unauthorized user", ex);
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProductNotAvailableException(ProductNotAvailableException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, "product unavailable", ex);
    }

    @ExceptionHandler(CartNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCartNotFoundException(CartNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, "cart not found", ex);
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
