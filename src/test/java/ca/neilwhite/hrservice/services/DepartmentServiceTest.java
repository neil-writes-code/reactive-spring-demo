package ca.neilwhite.hrservice.services;

import ca.neilwhite.hrservice.exceptions.DepartmentAlreadyExistsException;
import ca.neilwhite.hrservice.exceptions.DepartmentNotFoundException;
import ca.neilwhite.hrservice.models.Department;
import ca.neilwhite.hrservice.models.Employee;
import ca.neilwhite.hrservice.models.requests.CreateDepartmentRequest;
import ca.neilwhite.hrservice.repositories.DepartmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
    @Mock
    private DepartmentRepository repository;
    @InjectMocks
    private DepartmentService service;

    @Test
    @DisplayName("getDepartments() should return 2 Departments")
    void getDepartments_shouldReturnDepartments() {
        when(this.repository.findAll()).thenReturn(Flux.fromIterable(List.of(stubbedDevDepartment(), stubbedHRDepartment())));

        this.service.getDepartments()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getDepartment(1) should return a Department")
    void getDepartment_shouldReturnDepartment() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedDevDepartment()));

        this.service.getDepartment(1L)
                .as(StepVerifier::create)
                .consumeNextWith(department -> assertEquals(stubbedDevDepartment(), department))
                .verifyComplete();
    }

    @Test
    @DisplayName("getDepartment(3) should throw DepartmentNotFoundException")
    void getDepartment_shouldThrowDepartmentNotFound() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.empty());

        this.service.getDepartment(3L)
                .as(StepVerifier::create)
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("getDepartmentEmployees(1, null) should return 2 Employees")
    void getDepartmentEmployees_shouldReturnEmployees() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedDevDepartment()));

        this.service.getDepartmentEmployees(1L, null)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getDepartmentEmployees(1, true) should return 1 Employees")
    void getDepartmentEmployees_FullTime_shouldReturnEmployees() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedDevDepartment()));

        this.service.getDepartmentEmployees(1L, true)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("getDepartmentEmployees(1, false) should return 1 Employees")
    void getDepartmentEmployees_PartTime_shouldReturnEmployees() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedDevDepartment()));

        this.service.getDepartmentEmployees(1L, false)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("getDepartmentEmployees(3, null) should throw DepartmentNotFoundException")
    void getDepartmentEmployees_shouldThrowDepartmentNotFound() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.empty());

        this.service.getDepartmentEmployees(3L, null)
                .as(StepVerifier::create)
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("createDepartment(request) should return a Department")
    void createDepartment_shouldReturnDepartment() {
        Department accounting = Department.builder()
                .id(3L)
                .name("Accounting")
                .build();

        when(this.repository.findByName(anyString())).thenReturn(Mono.empty());
        when(this.repository.save(any(Department.class))).thenReturn(Mono.just(accounting));

        this.service.createDepartment(new CreateDepartmentRequest("Accounting"))
                .as(StepVerifier::create)
                .consumeNextWith(department -> assertEquals(accounting, department))
                .verifyComplete();
    }

    @Test
    @DisplayName("createDepartment(request) should throw DepartmentAlreadyExistsException")
    void createDepartment_shouldThrowDepartmentAlreadyExists() {
        Department accounting = Department.builder()
                .id(3L)
                .name("Accounting")
                .build();

        when(this.repository.findByName(anyString())).thenReturn(Mono.just(accounting));

        this.service.createDepartment(new CreateDepartmentRequest("Accounting"))
                .as(StepVerifier::create)
                .expectError(DepartmentAlreadyExistsException.class)
                .verify();
    }

    @Test
    @DisplayName("updateDepartment(1, department) should return an updated Department")
    void updateDepartment_shouldReturnDepartment() {
        Department updatedDevDepartment = stubbedDevDepartment();

        Employee manager = Employee.builder()
                .id(6L)
                .firstName("Sally")
                .lastName("Smith")
                .position("Director of Software Development")
                .fullTime(true)
                .build();

        updatedDevDepartment.setManager(manager);

        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedDevDepartment()));
        when(this.repository.save(any(Department.class))).thenReturn(Mono.just(updatedDevDepartment));

        this.service.updateDepartment(1L, updatedDevDepartment)
                .as(StepVerifier::create)
                .consumeNextWith(department -> assertEquals(updatedDevDepartment, department))
                .verifyComplete();
    }

    @Test
    @DisplayName("updateDepartment(3, department) should throw DepartmentNotFoundException")
    void updateDepartment_shouldThrowDepartmentNotFound() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.empty());

        this.service.updateDepartment(3L, stubbedDevDepartment())
                .as(StepVerifier::create)
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("deleteDepartment(1) should complete")
    void deleteDepartment_shouldDeleteDepartment() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.just(stubbedDevDepartment()));
        when(this.repository.delete(any(Department.class))).thenReturn(Mono.empty());

        this.service.deleteDepartment(stubbedDevDepartment().getId())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteDepartment(3) should throw DepartmentNotFound")
    void deleteDepartment_shouldThrowDepartmentNotFound() {
        when(this.repository.findById(anyLong())).thenReturn(Mono.empty());

        this.service.deleteDepartment(3L)
                .as(StepVerifier::create)
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }

    private Department stubbedDevDepartment() {
        return Department.builder()
                .id(1L)
                .name("Software Development")
                .manager(Employee.builder()
                        .id(1L)
                        .firstName("Bob")
                        .lastName("Steeves")
                        .position("Director of Software Development")
                        .fullTime(true)
                        .build())
                .employees(List.of(
                        Employee.builder()
                                .id(2L)
                                .firstName("Neil")
                                .lastName("White")
                                .position("Software Developer")
                                .fullTime(true)
                                .build(),
                        Employee.builder()
                                .id(3L)
                                .firstName("Joanna")
                                .lastName("Bernier")
                                .position("Software Tester")
                                .fullTime(false)
                                .build()))
                .build();
    }

    private Department stubbedHRDepartment() {
        return Department.builder()
                .id(2L)
                .name("HR")
                .manager(Employee.builder()
                        .id(4L)
                        .firstName("Cathy")
                        .lastName("Ouellette")
                        .position("Director of Human Resources")
                        .fullTime(true)
                        .build())
                .employees(List.of(
                        Employee.builder()
                                .id(5L)
                                .firstName("Alysha")
                                .lastName("Rogers")
                                .position("Intraday Analyst")
                                .fullTime(true)
                                .build()))
                .build();
    }
}