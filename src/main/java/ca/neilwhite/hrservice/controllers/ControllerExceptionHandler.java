package ca.neilwhite.hrservice.controllers;

import ca.neilwhite.hrservice.exceptions.DepartmentAlreadyExistsException;
import ca.neilwhite.hrservice.exceptions.DepartmentNotFoundException;
import ca.neilwhite.hrservice.exceptions.EmployeeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({
            DepartmentNotFoundException.class,
            EmployeeNotFoundException.class
    })
    ResponseEntity<String> handleNotFound(RuntimeException exception) {
        log.debug("handling exception:: " + exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({DepartmentAlreadyExistsException.class})
    ResponseEntity<String> handleBadRequest(RuntimeException exception) {
        log.debug("handling exception:: " + exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<List<String>> handleException(WebExchangeBindException e) {
        List<String> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }
}
