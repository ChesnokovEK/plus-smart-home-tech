package ru.yandex.practicum.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private Throwable cause;
    private List<StackTraceElement> stackTrace;
    private String httpStatus;
    private String userMessage;
    private String message;
    private List<Throwable> suppressed;
    private String localizedMessage;
}