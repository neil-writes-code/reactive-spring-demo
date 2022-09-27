DELETE FROM department_employees;
DELETE FROM department_managers;
DELETE FROM departments;
DELETE FROM employees;

INSERT INTO departments(id, name)
VALUES (10, 'Software Development'),
       (20, 'HR');

INSERT INTO employees(id, first_name, last_name, position, is_full_time)
VALUES (10, 'Bob', 'Steeves', 'Director of Software Development', true),
       (11, 'Neil', 'White', 'Software Developer', true),
       (12, 'Joanna', 'Bernier', 'Software Tester', false),
       (13, 'Cathy', 'Ouellette', 'Director of Human Resources', true),
       (14, 'Alysha', 'Rogers', 'Intraday Analyst', true);

INSERT INTO department_managers(department_id, employee_id)
VALUES (10, 10),
       (20, 13);

INSERT INTO department_employees(department_id, employee_id)
VALUES (10, 11),
       (10, 12),
       (20, 14);