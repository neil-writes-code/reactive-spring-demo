package ca.neilwhite.hrservice.exceptions;

public class DepartmentNotFoundException extends RuntimeException{
    public DepartmentNotFoundException(Long id) {
        super(String.format("Department not found. Id: %d", id));
    }
}
