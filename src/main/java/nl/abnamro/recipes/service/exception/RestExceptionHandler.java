package nl.abnamro.recipes.service.exception;

import nl.abnamro.recipes.dto.ErrorDto;
import nl.abnamro.recipes.utils.Errors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { NotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected List<ErrorDto> handleNotFound(NotFoundException ex, WebRequest request) {
        return ex.getErrors();
    }

    @ExceptionHandler(value = { AlreadyExistException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    protected List<ErrorDto> handleConflict(AlreadyExistException ex, WebRequest request) {
        return ex.getErrors();
    }

    @ExceptionHandler(value = { BadRequestException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected List<ErrorDto> handleBadRequest(BadRequestException ex, WebRequest request) {
        return ex.getErrors();
    }

    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected List<ErrorDto> handleServerError(Exception ex, WebRequest request) {
        return Arrays.asList(new ErrorDto(Errors.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,HttpHeaders headers, HttpStatus status, WebRequest request) {

        BindingResult result = ex.getBindingResult();

        var fieldErrors =
                result.getFieldErrors().stream()
                        .map((x -> new ErrorDto(x.getField(), x.getDefaultMessage())));

        return ResponseEntity.badRequest().body(fieldErrors);
    }

}
