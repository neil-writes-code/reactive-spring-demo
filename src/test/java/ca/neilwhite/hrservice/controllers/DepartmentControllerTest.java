package ca.neilwhite.hrservice.controllers;

import ca.neilwhite.hrservice.exceptions.DepartmentAlreadyExistsException;
import ca.neilwhite.hrservice.exceptions.DepartmentNotFoundException;
import ca.neilwhite.hrservice.models.Department;
import ca.neilwhite.hrservice.models.Employee;
import ca.neilwhite.hrservice.models.requests.CreateDepartmentRequest;
import ca.neilwhite.hrservice.repositories.DepartmentRepositoryImpl;
import ca.neilwhite.hrservice.repositories.EmployeeRepository;
import ca.neilwhite.hrservice.services.DepartmentService;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(controllers = DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private DepartmentRepositoryImpl repository;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private DepartmentService service;

    @Test
    @DisplayName("GET /departments should return 1 Department")
    void getDepartments_shouldReturnDepartments() {
        when(this.service.getDepartments()).thenReturn(Flux.just(stubbedDevDepartment()));

        client.get()
                .uri("/departments")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Department.class).hasSize(1)
                .consumeWith(departments -> assertEquals(List.of(stubbedDevDepartment()), departments.getResponseBody()));
    }

    @Test
    @DisplayName("GET /departments/1 should return a Department")
    void getDepartment_shouldReturnDepartment() {
        when(this.service.getDepartment(anyLong())).thenReturn(Mono.just(stubbedDevDepartment()));

        client.get()
                .uri("/departments/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Department.class)
                .consumeWith(department -> assertEquals(stubbedDevDepartment(), department.getResponseBody()));
    }

    @Test
    @DisplayName("GET /departments/10 should return DepartmentNotFoundException")
    void getDepartment_shouldReturnDepartmentNotFound() {
        when(this.service.getDepartment(anyLong())).thenThrow(new DepartmentNotFoundException(10L));

        client.get()
                .uri("/departments/10")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(exception -> assertEquals("Department not found. Id: 10", exception.getResponseBody()));
    }

    @Test
    @DisplayName("GET /departments/1/employees should return 2 Employees")
    void getDepartmentEmployees_shouldReturnEmployees() {
        when(this.service.getDepartmentEmployees(anyLong(), isNull()))
                .thenReturn(Flux.fromIterable(stubbedDevDepartment().getEmployees()));

        client.get()
                .uri("/departments/1/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class).hasSize(2)
                .consumeWith(employees -> assertEquals(stubbedDevDepartment().getEmployees(), employees.getResponseBody()));
    }

    @Test
    @DisplayName("GET /departments/1/employees?fullTime=true should return 1 Employees")
    void getFullTimeDepartmentEmployees_shouldReturnEmployees() {
        when(this.service.getDepartmentEmployees(anyLong(), anyBoolean()))
                .thenReturn(Flux.just(stubbedDevDepartment().getEmployees().get(0)));

        client.get()
                .uri("/departments/1/employees?fullTime=true")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class).hasSize(1)
                .consumeWith(employees -> assertEquals(List.of(stubbedDevDepartment().getEmployees().get(0)), employees.getResponseBody()));
    }

    @Test
    @DisplayName("POST /departments should return a Department")
    void createDepartment_shouldReturnDepartment() {
        Department accounting = Department.builder().name("Accounting").build();

        when(this.service.createDepartment(any(CreateDepartmentRequest.class))).thenReturn(Mono.just(accounting));

        client.post().uri("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateDepartmentRequest("Accounting"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Department.class)
                .consumeWith(department -> assertEquals(accounting, department.getResponseBody()));
    }

    @Test
    @DisplayName("POST /departments should return DepartmentAlreadyExistsException")
    void createDepartment_shouldReturnDepartmentAlreadyExists() {
        when(this.service.createDepartment(any(CreateDepartmentRequest.class))).thenThrow(new DepartmentAlreadyExistsException("Accounting"));

        client.post().uri("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateDepartmentRequest("Accounting"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(exception -> assertEquals("Department with name \"Accounting\" already exists.", exception.getResponseBody()));
    }

    @Test
    @DisplayName("POST /departments should return HTTP 400 - validation issue")
    void createDepartment_shouldReturnBadRequest() {
        client.post().uri("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateDepartmentRequest(""))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("PUT /departments/1 should return a Department")
    void updateDepartment_shouldReturnDepartment() {
        when(this.service.updateDepartment(anyLong(), any(Department.class))).thenReturn(Mono.just(stubbedDevDepartment()));

        client.put().uri("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stubbedDevDepartment())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Department.class)
                .consumeWith(department -> assertEquals(stubbedDevDepartment(), department.getResponseBody()));
    }

    @Test
    @DisplayName("PUT /departments/10 should return DepartmentNotFoundException")
    void updateDepartment_shouldReturnDepartmentNotFound() {
        when(this.service.updateDepartment(anyLong(), any(Department.class))).thenThrow(new DepartmentNotFoundException(10L));

        client.put().uri("/departments/10")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stubbedDevDepartment())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(exception -> assertEquals("Department not found. Id: 10", exception.getResponseBody()));
    }

    @Test
    @DisplayName("DELETE /departments/1 should return OK")
    void deleteDepartment_shouldReturnOK() {
        when(this.service.deleteDepartment(anyLong())).thenReturn(Mono.empty());

        client.delete().uri("/departments/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("DELETE /departments/10 should return DepartmentNotFoundException")
    void deleteDepartment_shouldReturnDepartmentNotFound() {
        when(this.service.deleteDepartment(anyLong())).thenThrow(new DepartmentNotFoundException(10L));

        client.delete().uri("/departments/10")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(exception -> assertEquals("Department not found. Id: 10", exception.getResponseBody()));
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
}