package tn.esprit.autoloc.autolocapi.service;

import tn.esprit.autoloc.autolocapi.domain.Employee;

import java.util.List;

public interface IEmployeeService {
    Employee create(Employee employee);
    Employee findById(Long id);
    List<Employee> findAll();
    Employee update(Long id, Employee employee);
    void deleteById(Long id);
}