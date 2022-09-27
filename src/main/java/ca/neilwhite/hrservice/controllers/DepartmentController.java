package ca.neilwhite.hrservice.controllers;

import ca.neilwhite.hrservice.models.Department;
import ca.neilwhite.hrservice.models.Employee;
import ca.neilwhite.hrservice.models.requests.CreateDepartmentRequest;
import ca.neilwhite.hrservice.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService service;

    @GetMapping
    public Flux<Department> getDepartments() {
        return this.service.getDepartments();
    }

    @GetMapping("/{id}")
    public Mono<Department> getDepartment(@PathVariable Long id) {
        return this.service.getDepartment(id);
    }

    @GetMapping("/{id}/employees")
    public Flux<Employee> getDepartmentEmployees(@PathVariable Long id, @RequestParam(name = "fullTime", required = false) Boolean isFullTime) {
        return this.service.getDepartmentEmployees(id, isFullTime);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Department> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        return this.service.createDepartment(request);
    }

    @PutMapping("/{id}")
    public Mono<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        return this.service.updateDepartment(id, department);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteDepartment(@PathVariable Long id) {
        return this.service.deleteDepartment(id);
    }
}
