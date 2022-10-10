package ca.neilwhite.hrservice.repositories;

import ca.neilwhite.hrservice.models.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {
    private final EmployeeRepository employeeRepository;
    private final DatabaseClient client;
    private static final String SELECT_QUERY = """
            SELECT d.id d_id, d.name d_name, m.id m_id, m.first_name m_firstName, m.last_name m_lastName,
                m.position m_position, m.is_full_time m_isFullTime, e.id e_id, e.first_name e_firstName,
                e.last_name e_lastName, e.position e_position, e.is_full_time e_isFullTime
            FROM departments d
            LEFT JOIN department_managers dm ON dm.department_id = d.id
            LEFT JOIN employees m ON m.id = dm.employee_id
            LEFT JOIN department_employees de ON de.department_id = d.id
            LEFT JOIN employees e ON e.id = de.employee_id
            """;

    /**
     * Returns all Departments.
     *
     * @return Flux of {@link Department}
     */
    @Override
    public Flux<Department> findAll() {
        return client.sql(SELECT_QUERY)
                .fetch()
                .all()
                .bufferUntilChanged(result -> result.get("d_id"))
                .flatMap(Department::fromRows);
    }

    /**
     * Returns a Department by ID.
     *
     * @param id Department ID
     * @return Mono of {@link Department}
     */
    @Override
    public Mono<Department> findById(long id) {
        return client.sql(String.format("%s WHERE d.id = :id", SELECT_QUERY))
                .bind("id", id)
                .fetch()
                .all()
                .bufferUntilChanged(result -> result.get("d_id"))
                .flatMap(Department::fromRows)
                .singleOrEmpty();
    }

    /**
     * Returns a Department by name.
     *
     * @param name Department Name
     * @return Mono of {@link Department}
     */
    @Override
    public Mono<Department> findByName(String name) {
        return client.sql(String.format("%s WHERE d.name = :name", SELECT_QUERY))
                .bind("name", name)
                .fetch()
                .all()
                .bufferUntilChanged(result -> result.get("d_id"))
                .flatMap(Department::fromRows)
                .singleOrEmpty();
    }

    /**
     * Saves and returns a Department.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    @Override
    @Transactional
    public Mono<Department> save(Department department) {
        return this.saveDepartment(department)
                .flatMap(this::saveManager)
                .flatMap(this::saveEmployees)
                .flatMap(this::deleteDepartmentManagers)
                .flatMap(this::saveDepartmentManagers)
                .flatMap(this::deleteDepartmentEmployees)
                .flatMap(this::saveDepartmentEmployees);
    }

    /**
     * Deletes a Department.
     *
     * @param department {@link Department}
     * @return Mono of {@link Void}
     */
    @Override
    @Transactional
    public Mono<Void> delete(Department department) {
        return this.deleteDepartmentManagers(department)
                .flatMap(this::deleteDepartmentEmployees)
                .flatMap(this::deleteDepartment)
                .then();
    }

    /**
     * Saves a Department.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    Mono<Department> saveDepartment(Department department) {
        if (department.getId() == null) {
            return client.sql("INSERT INTO departments(name) VALUES(:name)")
                    .bind("name", department.getName())
                    .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                    .fetch().first()
                    .doOnNext(result -> department.setId(Long.parseLong(result.get("id").toString())))
                    .thenReturn(department);
        } else {
            return this.client.sql("UPDATE departments SET name = :name WHERE id = :id")
                    .bind("name", department.getName())
                    .bind("id", department.getId())
                    .fetch().first()
                    .thenReturn(department);
        }
    }

    /**
     * Saves a Department Manager.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    Mono<Department> saveManager(Department department) {
        return Mono.justOrEmpty(department.getManager())
                .flatMap(employeeRepository::save)
                .doOnNext(department::setManager)
                .thenReturn(department);
    }

    /**
     * Saves Department Employees.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    Mono<Department> saveEmployees(Department department) {
        return Flux.fromIterable(department.getEmployees())
                .flatMap(this.employeeRepository::save)
                .collectList()
                .doOnNext(department::setEmployees)
                .thenReturn(department);
    }

    /**
     * Saves the relationship between Department and Manager.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    Mono<Department> saveDepartmentManagers(Department department) {
        return Mono.justOrEmpty(department.getManager())
                .flatMap(manager -> client.sql("INSERT INTO department_managers(department_id, employee_id) "
                                + "VALUES (:id, :empId)")
                        .bind("id", department.getId())
                        .bind("empId", manager.getId())
                        .fetch().rowsUpdated())
                .thenReturn(department);
    }

    /**
     * Saves the relationship between Department and Employees.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    Mono<Department> saveDepartmentEmployees(Department department) {
        return Flux.fromIterable(department.getEmployees())
                .flatMap(employee -> client.sql("INSERT INTO department_employees(department_id, employee_id) "
                                + "VALUES (:id, :empId)")
                        .bind("id", department.getId())
                        .bind("empId", employee.getId())
                        .fetch().rowsUpdated())
                .collectList()
                .thenReturn(department);
    }

    /**
     * Deletes a Department.
     *
     * @param department {@link Department}
     * @return Mono of {@link Void}
     */
    Mono<Void> deleteDepartment(Department department) {
        return client.sql("DELETE FROM departments WHERE id = :id")
                .bind("id", department.getId())
                .fetch().first()
                .then();
    }

    /**
     * Deletes the relationship between Department and Manager.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    Mono<Department> deleteDepartmentManagers(Department department) {
        return client.sql("DELETE FROM department_managers WHERE department_id = :id")
                .bind("id", department.getId())
                .fetch().rowsUpdated()
                .thenReturn(department);

    }

    /**
     * Deletes the relationship between Department and Employees.
     *
     * @param department {@link Department}
     * @return Mono of {@link Department}
     */
    Mono<Department> deleteDepartmentEmployees(Department department) {
        return client.sql("DELETE FROM department_employees WHERE department_id = :id")
                .bind("id", department.getId())
                .fetch().rowsUpdated()
                .thenReturn(department);
    }
}
