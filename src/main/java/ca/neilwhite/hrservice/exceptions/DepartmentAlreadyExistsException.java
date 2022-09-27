package ca.neilwhite.hrservice.exceptions;

public class DepartmentAlreadyExistsException extends RuntimeException {
    public DepartmentAlreadyExistsException(String name) {
        super(String.format("Department with name \"%s\" already exists.", name));
    }
}
