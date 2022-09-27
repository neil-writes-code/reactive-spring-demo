package ca.neilwhite.hrservice.services;

import ca.neilwhite.hrservice.exceptions.DepartmentAlreadyExistsException;
import ca.neilwhite.hrservice.exceptions.DepartmentNotFoundException;
import ca.neilwhite.hrservice.models.Department;
import ca.neilwhite.hrservice.models.Employee;
import ca.neilwhite.hrservice.models.requests.CreateDepartmentRequest;
import ca.neilwhite.hrservice.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;

    /**
     * Returns all Departments.
     *
     * @return Flux of {@link Department}
     */
    public Flux<Department> getDepartments() {
        return this.repository.findAll();
    }

    /**
     * Returns a Department by ID.
     *
     * @param id Department ID
     * @return Mono of {@link Department}
     */
    public Mono<Department> getDepartment(Long id) {
        return this.repository.findById(id)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException(id)));
    }

    /**
     * Returns the Employees of a Department by ID.
     *
     * @param id         Department ID
     * @param isFullTime Filter employees on full time status
     * @return Flux of {@link Employee}
     */
    public Flux<Employee> getDepartmentEmployees(Long id, Boolean isFullTime) {
        if (isFullTime != null) {
            return this.repository.findById(id)
                    .switchIfEmpty(Mono.error(new DepartmentNotFoundException(id)))
                    .flatMapMany(department ->
                            Flux.fromStream(department.getEmployees()
                                    .stream()
                                    .filter(employee -> employee.isFullTime() == isFullTime)));
        } else {
            return this.repository.findById(id)
                    .switchIfEmpty(Mono.error(new DepartmentNotFoundException(id)))
                    .flatMapMany(department -> Flux.fromIterable(department.getEmployees()));
        }
    }

    /**
     * Creates and returns a new Department.
     *
     * @param request {@link CreateDepartmentRequest}
     * @return Mono of {@link Department}
     */
    public Mono<Department> createDepartment(CreateDepartmentRequest request) {
        return this.repository.findByName(request.name())
                .flatMap(department -> Mono.error(new DepartmentAlreadyExistsException(department.getName())))
                .defaultIfEmpty(Department.builder().name(request.name()).build()).cast(Department.class)
                .flatMap(this.repository::save);
    }

    /**
     * Updates and returns a Department.
     *
     * @param id         Department ID
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    public Mono<Department> updateDepartment(Long id, Department department) {
        return this.repository.findById(id)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException(id)))
                .doOnNext(currentDepartment -> {
                    currentDepartment.setName(department.getName());

                    if(department.getManager().isPresent()){
                        currentDepartment.setManager(department.getManager().get());
                    }

                    currentDepartment.setEmployees(department.getEmployees());
                })
                .flatMap(this.repository::save);
    }

    /**
     * Deletes a Department by ID.
     *
     * @param id Department ID
     * @return Mono of {@link Void}
     */
    public Mono<Void> deleteDepartment(Long id) {
        return this.repository.findById(id)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException(id)))
                .flatMap(this.repository::delete)
                .then();
    }
}


