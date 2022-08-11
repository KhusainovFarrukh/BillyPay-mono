package kh.farrukh.billypay.global.exception.handling;

import kh.farrukh.billypay.global.exception.ApiException;
import kh.farrukh.billypay.global.exception.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * It handles exceptions thrown by the application
 * and returns a response with a message and a status code
 */
@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageSource messageSource;

    /**
     * It handles all custom exceptions
     *
     * @param exception The exception object that was thrown.
     * @param locale    The locale of the user.
     * @return A ResponseEntity<Object>
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception, Locale locale) {
        exception.printStackTrace();
        return new ResponseEntity<>(
                new ErrorResponse(
                        messageSource.getMessage(
                                exception.getMessageId(),
                                exception.getMessageArgs(),
                                locale
                        ),
                        exception.getHttpStatus(),
                        exception.getHttpStatus().value(),
                        ZonedDateTime.now()
                ),
                exception.getHttpStatus()
        );
    }

    /**
     * It handles all other exceptions (not handled exceptions)
     *
     * @param exception The exception object that was thrown.
     * @return A ResponseEntity<Object>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception exception, Locale locale) {
        exception.printStackTrace();
        return new ResponseEntity<>(
                new ErrorResponse(
                        messageSource.getMessage(
                                ExceptionMessages.EXCEPTION_UNKNOWN,
                                null,
                                locale
                        ) + ": " + exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ZonedDateTime.now()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
