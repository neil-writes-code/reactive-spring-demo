package ca.neilwhite.hrservice.exceptions;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long id) {
        super(String.format("Employee not found. Id: %d", id));
    }
}
