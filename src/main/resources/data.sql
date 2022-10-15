DELETE FROM department_managers;
DELETE FROM department_employees;
DELETE FROM departments;
DELETE FROM employees;

ALTER SEQUENCE departments_id_seq RESTART WITH 1;
ALTER SEQUENCE employees_id_seq RESTART WITH 1;

INSERT INTO departments(name)
VALUES ('Software Development'),
       ('HR');

INSERT INTO employees(first_name, last_name, position, is_full_time)
VALUES ('Bob', 'Steeves', 'Director of Software Development', true),
       ('Neil', 'White', 'Software Developer', true),
       ('Joanna', 'Bernier', 'Software Tester', false),
       ('Cathy', 'Ouellette', 'Director of Human Resources', true),
       ('Alysha', 'Rogers', 'Intraday Analyst', true);

INSERT INTO department_managers(department_id, employee_id)
VALUES (1, 1),
       (2, 4);

INSERT INTO department_employees(department_id, employee_id)
VALUES (1, 2),
       (1, 3),
       (2, 5);