package ca.neilwhite.hrservice.controllers;

import ca.neilwhite.hrservice.exceptions.EmployeeNotFoundException;
import ca.neilwhite.hrservice.models.Employee;
import ca.neilwhite.hrservice.models.requests.CreateEmployeeRequest;
import ca.neilwhite.hrservice.repositories.EmployeeRepository;
import ca.neilwhite.hrservice.services.EmployeeService;
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
@WebFluxTest(controllers = EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private EmployeeService service;

    @Test
    @DisplayName("GET /employees should return 1 Employee")
    void getEmployees_shouldReturnEmployees() {
        when(this.service.getEmployees(isNull(), isNull())).thenReturn(Flux.just(stubbedEmployee()));

        client.get()
                .uri("/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class).hasSize(1)
                .consumeWith(employees -> assertEquals(List.of(stubbedEmployee()), employees.getResponseBody()));
    }

    @Test
    @DisplayName("GET /employees?position=Software%20Developer should return 1 Employee")
    void getEmployeesByPosition_shouldReturnEmployee() {
        when(this.service.getEmployees(anyString(), isNull())).thenReturn(Flux.just(stubbedEmployee()));

        client.get()
                .uri("/employees?position=Software%20Developer")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class).hasSize(1)
                .consumeWith(employees -> assertEquals(List.of(stubbedEmployee()), employees.getResponseBody()));
    }

    @Test
    @DisplayName("GET /employees?fullTime=true should return 1 Employee")
    void getEmployeesByFullTime_shouldReturnEmployee() {
        when(this.service.getEmployees(isNull(), anyBoolean())).thenReturn(Flux.just(stubbedEmployee()));

        client.get()
                .uri("/employees?fullTime=true")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class).hasSize(1)
                .consumeWith(employees -> assertEquals(List.of(stubbedEmployee()), employees.getResponseBody()));
    }

    @Test
    @DisplayName("GET /employees?position=Software%20Developer&fullTime=true should return 1 Employee")
    void getEmployeesByPositionAndFullTime_shouldReturnEmployee() {
        when(this.service.getEmployees(anyString(), anyBoolean())).thenReturn(Flux.just(stubbedEmployee()));

        client.get()
                .uri("/employees?position=Software%20Developer&fullTime=true")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class).hasSize(1)
                .consumeWith(employees -> assertEquals(List.of(stubbedEmployee()), employees.getResponseBody()));
    }

    @Test
    @DisplayName("GET /employees/1 should return an Employee")
    void getEmployee_shouldReturnEmployee() {
        when(this.service.getEmployee(anyLong())).thenReturn(Mono.just(stubbedEmployee()));

        client.get()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Employee.class)
                .consumeWith(employee -> assertEquals(stubbedEmployee(), employee.getResponseBody()));
    }

    @Test
    @DisplayName("GET /employees/2 should return EmployeeNotFoundException")
    void getEmployee_shouldReturnEmployeeNotFound() {
        when(this.service.getEmployee(anyLong())).thenThrow(new EmployeeNotFoundException(2L));

        client.get()
                .uri("/employees/2")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(exception -> assertEquals("Employee not found. Id: 2", exception.getResponseBody()));
    }

    @Test
    @DisplayName("POST /employees should return an Employee")
    void createEmployee_shouldReturnEmployee() {
        Employee newEmployee = Employee.builder()
                .firstName("Bob")
                .lastName("Walker")
                .position("Dog Walker")
                .fullTime(false)
                .build();

        when(this.service.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(Mono.just(newEmployee));

        client.post().uri("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateEmployeeRequest("Bob", "Walker", "Dog Walker", false))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Employee.class)
                .consumeWith(employee -> assertEquals(newEmployee, employee.getResponseBody()));
    }

    @Test
    @DisplayName("PUT /employees/1 should return an Employee")
    void updateEmployee_shouldReturnEmployee() {
        when(this.service.updateEmployee(anyLong(), any(Employee.class))).thenReturn(Mono.just(stubbedEmployee()));

        client.put().uri("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stubbedEmployee())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Employee.class)
                .consumeWith(employee -> assertEquals(stubbedEmployee(), employee.getResponseBody()));
    }

    @Test
    @DisplayName("PUT /employees/2 should return EmployeeNotFoundException")
    void updateEmployee_shouldReturnEmployeeNotFound() {

        when(this.service.updateEmployee(anyLong(), any(Employee.class))).thenThrow(new EmployeeNotFoundException(2L));

        client.put().uri("/employees/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stubbedEmployee())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(exception -> assertEquals("Employee not found. Id: 2", exception.getResponseBody()));
    }

    @Test
    @DisplayName("DELETE /employees/1 should return OK")
    void deleteEmployee_shouldReturnOK() {
        when(this.service.deleteEmployee(anyLong())).thenReturn(Mono.empty());

        client.delete().uri("/employees/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("DELETE /employees/2 should return EmployeeNotFoundException")
    void deleteEmployee_shouldReturnEmployeeNotFound() {
        when(this.service.deleteEmployee(anyLong())).thenThrow(new EmployeeNotFoundException(2L));

        client.delete().uri("/employees/2")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(exception -> assertEquals("Employee not found. Id: 2", exception.getResponseBody()));
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