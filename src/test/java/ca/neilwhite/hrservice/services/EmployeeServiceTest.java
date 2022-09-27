package ca.neilwhite.hrservice.services;

import ca.neilwhite.hrservice.exceptions.EmployeeNotFoundException;
import ca.neilwhite.hrservice.models.Employee;
import ca.neilwhite.hrservice.models.requests.CreateEmployeeRequest;
import ca.neilwhite.hrservice.repositories.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository repository;
    @InjectMocks
    private EmployeeService service;

    @Test
    @DisplayName("getEmployees(null, null) should return 1 Employee")
    void getEmployees_shouldReturnEmployees() {
        when(this.repository.findAll()).thenReturn(Flux.just(stubbedEmployee()));

        this.service.getEmployees(null, null)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("getEmployees(\"Software Developer\", null) should return 1 Employee")
    void getEmployeesByPosition_shouldReturnEmployees() {
        when(this.repository.findAllByPosition(anyString())).thenReturn(Flux.just(stubbedEmployee()));

        this.service.getEmployees("Software Developer", null)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("getEmployees(null, true) should return 1 Employee")
    void getEmployeesByFullTime_shouldReturnEmployees() {
        when(this.repository.findAllByFullTime(anyBoolean())).thenReturn(Flux.just(stubbedEmployee()));

        this.service.getEmployees(null, true)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("getEmployees(\"Software Developer\", true) should return 1 Employee")
    void getEmployeesByPositionAndFullTime_shouldReturnEmployees() {
        when(this.repository.findAllByPositionAndFullTime(anyString(), anyBoolean())).thenReturn(Flux.just(stubbedEmployee()));

        this.service.getEmployees("Software Developer", true)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("getEmployee(1) should return an Employee")
    void getEmployee_shouldReturnEmployee() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedEmployee()));

        this.service.getEmployee(1L)
                .as(StepVerifier::create)
                .consumeNextWith(employee -> assertEquals(stubbedEmployee(), employee))
                .verifyComplete();
    }

    @Test
    @DisplayName("getEmployee(2) should throw EmployeeNotFoundException")
    void getEmployee_shouldThrowEmployeeNotFound() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.empty());

        this.service.getEmployee(2L)
                .as(StepVerifier::create)
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("createEmployee(request) should return an Employee")
    void createEmployee_shouldReturnEmployee() {
        Employee newEmployee = Employee.builder()
                .firstName("Bob")
                .lastName("Walker")
                .position("Dog Walker")
                .fullTime(false)
                .build();

        when(this.repository.save(any(Employee.class))).thenReturn(Mono.just(newEmployee));

        this.service.createEmployee(new CreateEmployeeRequest("Bob", "Walker", "Dog Walker", false))
                .as(StepVerifier::create)
                .consumeNextWith(employee -> assertEquals(newEmployee, employee))
                .verifyComplete();
    }

    @Test
    @DisplayName("updateEmployee(1, employee) should return an updated Employee")
    void updateEmployee_shouldReturnEmployee() {
        Employee updatedEmployee = stubbedEmployee();

        updatedEmployee.setFirstName("George");

        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedEmployee()));
        when(this.repository.save(any(Employee.class))).thenReturn(Mono.just(updatedEmployee));

        this.service.updateEmployee(1L, updatedEmployee)
                .as(StepVerifier::create)
                .consumeNextWith(employee -> assertEquals(updatedEmployee, employee))
                .verifyComplete();
    }

    @Test
    @DisplayName("updateEmployee(2, employee) should throw EmployeeNotFoundException")
    void updateEmployee_shouldThrowEmployeeNotFound() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.empty());

        this.service.updateEmployee(2L, stubbedEmployee())
                .as(StepVerifier::create)
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("deleteEmployee(1) should complete")
    void deleteEmployee_shouldDeleteEmployee() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedEmployee()));
        when(this.repository.delete(any(Employee.class))).thenReturn(Mono.empty());

        this.service.deleteEmployee(1L)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteEmployee(2) should throw EmployeeNotFoundException")
    void deleteEmployee_shouldReturnEmployeeNotFound() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.empty());

        this.service.deleteEmployee(2L)
                .as(StepVerifier::create)
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    private Employee stubbedEmployee() {
        return Employee.builder()
                .id(1L)
                .firstName("Neil")
                .lastName("White")
                .position("Software Developer")
                .fullTime(true)
                .build();
    }
}