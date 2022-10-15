package ca.neilwhite.hrservice.repositories;

import ca.neilwhite.hrservice.models.Department;
import ca.neilwhite.hrservice.models.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class DepartmentRepositoryTest {
    @Autowired
    private DepartmentRepositoryImpl repository;

    @Test
    @DisplayName("findAll() should return 2 Departments")
    void findAll_shouldReturnDepartments() {
        this.repository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("findById(10) should return a Department")
    void findById_shouldReturnDepartment() {
        this.repository.findById(10)
                .as(StepVerifier::create)
                .consumeNextWith(department -> assertEquals(stubbedDevDepartment(), department))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById(3) should not return a Department")
    void findById_shouldNotReturnDepartment() {
        this.repository.findById(3)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByName(\"HR\") should return a Department")
    void findByName_shouldReturnDepartment() {
        this.repository.findByName("HR")
                .as(StepVerifier::create)
                .consumeNextWith(department -> assertEquals(stubbedHRDepartment(), department))
                .verifyComplete();
    }

    @Test
    @DisplayName("findByName(\"Accounting\") should not return a Department")
    void findByName_shouldNotReturnDepartment() {
        this.repository.findByName("Accounting")
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @DirtiesContext
    @ParameterizedTest
    @MethodSource("newDepartmentProvider")
    @DisplayName("save(department) should return a Department")
    void save_shouldSaveDepartment(Department newDepartment) {
        this.repository.save(newDepartment)
                .flatMap(department -> this.repository.findById(department.getId()))
                .as(StepVerifier::create)
                .consumeNextWith(department -> assertEquals(newDepartment, department))
                .verifyComplete();
    }

    @DirtiesContext
    @ParameterizedTest
    @MethodSource("updatedDepartmentProvider")
    @DisplayName("save(department) should return an updated Department")
    void save_shouldUpdateDepartment(Department updatedDepartment) {
        this.repository.save(updatedDepartment)
                .flatMap(department -> this.repository.findById(department.getId()))
                .as(StepVerifier::create)
                .consumeNextWith(department -> assertEquals(updatedDepartment, department))
                .verifyComplete();
    }

    @Test
    @DirtiesContext
    @DisplayName("delete(department) should delete a Department")
    void delete_shouldDeleteDepartment() {
        this.repository.delete(stubbedDevDepartment())
                .flatMap(__ -> this.repository.findById(stubbedDevDepartment().getId()))
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    private static Stream<Arguments> newDepartmentProvider() {
        Department newDepartmentNameOnly = Department.builder()
                .name("Accounting")
                .build();

        Department newDepartmentWithManager = Department.builder()
                .name("Accounting")
                .manager(stubbedDevDepartment().getManager().get())
                .build();

        Department newDepartmentWithEmployees = Department.builder()
                .name("Accounting")
                .employees(stubbedDevDepartment().getEmployees())
                .build();

        Department newDepartmentWithManagerAndEmployees = Department.builder()
                .name("Accounting")
                .manager(stubbedDevDepartment().getManager().get())
                .employees(stubbedDevDepartment().getEmployees())
                .build();

        return Stream.of(
                Arguments.of(newDepartmentNameOnly),
                Arguments.of(newDepartmentWithManager),
                Arguments.of(newDepartmentWithEmployees),
                Arguments.of(newDepartmentWithManagerAndEmployees)
        );
    }

    private static Stream<Arguments> updatedDepartmentProvider() {
        Department nameUpdatedDepartment = stubbedDevDepartment();
        nameUpdatedDepartment.setName("Software Engineering");

        Department managerUpdatedDepartment = stubbedDevDepartment();
        managerUpdatedDepartment.setManager(stubbedHRDepartment().getManager().get());

        Department employeesUpdatedDepartment = stubbedDevDepartment();
        employeesUpdatedDepartment.setEmployees(stubbedHRDepartment().getEmployees());

        Department removeManagerAndEmployees = stubbedDevDepartment();
        removeManagerAndEmployees.setManager(null);
        removeManagerAndEmployees.setEmployees(List.of());

        return Stream.of(
                Arguments.of(nameUpdatedDepartment),
                Arguments.of(managerUpdatedDepartment),
                Arguments.of(employeesUpdatedDepartment),
                Arguments.of(removeManagerAndEmployees)
        );
    }

    private static Department stubbedDevDepartment() {
        return Department.builder()
                .id(10L)
                .name("Software Development")
                .manager(Employee.builder()
                        .id(10L)
                        .firstName("Bob")
                        .lastName("Steeves")
                        .position("Director of Software Development")
                        .fullTime(true)
                        .build())
                .employees(List.of(
                        Employee.builder()
                                .id(11L)
                                .firstName("Neil")
                                .lastName("White")
                                .position("Software Developer")
                                .fullTime(true)
                                .build(),
                        Employee.builder()
                                .id(12L)
                                .firstName("Joanna")
                                .lastName("Bernier")
                                .position("Software Tester")
                                .fullTime(false)
                                .build()))
                .build();
    }

    private static Department stubbedHRDepartment() {
        return Department.builder()
                .id(20L)
                .name("HR")
                .manager(Employee.builder()
                        .id(13L)
                        .firstName("Cathy")
                        .lastName("Ouellette")
                        .position("Director of Human Resources")
                        .fullTime(true)
                        .build())
                .employees(List.of(
                        Employee.builder()
                                .id(14L)
                                .firstName("Alysha")
                                .lastName("Rogers")
                                .position("Intraday Analyst")
                                .fullTime(true)
                                .build()))
                .build();
    }
}