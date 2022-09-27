package ca.neilwhite.hrservice.repositories;

import ca.neilwhite.hrservice.models.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataR2dbcTest
@Testcontainers
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository repository;

    @Test
    @DirtiesContext
    @DisplayName("save() should return an Employee")
    void save_shouldReturnEmployee() {
        Employee newEmployee = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .position("Accountant")
                .fullTime(true)
                .build();

        this.repository.save(newEmployee).log()
                .as(StepVerifier::create)
                .consumeNextWith(employee -> assertEquals(newEmployee, employee))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAll() should return 5 Employees")
    void findAll_shouldReturnEmployees() {
        this.repository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById(11) should return an Employee")
    void findById_shouldReturnEmployee() {
        this.repository.findById(11L)
                .as(StepVerifier::create)
                .consumeNextWith(employee -> assertEquals(stubbedEmployee(), employee))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById(9) should not return an Employee")
    void findById_shouldNotReturnEmployee() {
        this.repository.findById(9L)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByPosition(\"Software Developer\") should return an Employee")
    void findAllByPosition_shouldReturnEmployee() {
        this.repository.findAllByPosition("Software Developer")
                .as(StepVerifier::create)
                .consumeNextWith(employee -> assertEquals(stubbedEmployee(), employee))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByPosition(\"Marketing\") should not return an Employee")
    void findAllByPosition_shouldNotReturnEmployee() {
        this.repository.findAllByPosition("Marketing")
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByFullTime(true) should return 4 Employees")
    void findAllByFullTime_True_shouldReturnEmployees() {
        this.repository.findAllByFullTime(true)
                .as(StepVerifier::create)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByFullTime(false) should return 1 Employee")
    void findAllByFullTime_False_shouldReturnEmployees() {
        this.repository.findAllByFullTime(false)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByFirstName(\"Neil\") should return an Employee")
    void findByFirstName_shouldReturnEmployee() {
        this.repository.findByFirstName("Neil")
                .as(StepVerifier::create)
                .consumeNextWith(employee -> assertEquals(stubbedEmployee(), employee))
                .verifyComplete();
    }

    @Test
    @DisplayName("findByFirstName(\"Steve\") should not return an Employee")
    void findByFirstName_shouldNotReturnEmployee() {
        this.repository.findByFirstName("Steve")
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    private Employee stubbedEmployee() {
        return Employee.builder()
                .id(11L)
                .firstName("Neil")
                .lastName("White")
                .position("Software Developer")
                .fullTime(true)
                .build();
    }
}