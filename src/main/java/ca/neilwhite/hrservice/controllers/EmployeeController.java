package ca.neilwhite.hrservice.controllers;

import ca.neilwhite.hrservice.models.Employee;
import ca.neilwhite.hrservice.models.requests.CreateEmployeeRequest;
import ca.neilwhite.hrservice.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService service;

    @GetMapping
    public Flux<Employee> getEmployees(@RequestParam(required = false) String position, @RequestParam(name = "fullTime", required = false) Boolean isFullTime) {
        return this.service.getEmployees(position, isFullTime);
    }

    @GetMapping("/{id}")
    public Mono<Employee> getEmployee(@PathVariable Long id) {
        return this.service.getEmployee(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Employee> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return this.service.createEmployee(request);
    }

    @PutMapping("/{id}")
    public Mono<Employee> updateEmployee(@PathVariable Long id, Employee employee) {
        return this.service.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteEmployee(@PathVariable Long id) {
        return this.service.deleteEmployee(id);
    }
}
