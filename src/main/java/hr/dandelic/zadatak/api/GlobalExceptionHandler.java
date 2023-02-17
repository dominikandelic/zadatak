package hr.dandelic.zadatak.api;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String INTERNAL_SERVER_ERROR = "internalServerError";

    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<RestApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new RestApiError(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<RestApiError> handleValidationException(ValidationException e, Locale locale) {
        String errorMessage = messageSource.getMessage(e.getMessage(), null, locale);
        return new ResponseEntity<>(new RestApiError(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiError> handleExceptions(Exception e, Locale locale) {
        String errorMessage = messageSource.getMessage(INTERNAL_SERVER_ERROR, null, locale);
        e.printStackTrace();
        return new ResponseEntity<>(new RestApiError(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
