package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.Employee;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService{

    Flux<Employee> listEmployees(String customerId);

    Mono<Employee> registerSigner(String customerId, Employee employee);

    Mono<Employee> registerHolder(String customerId, Employee employee);

    Mono<Employee> updateEmployee(String customerId, Employee employee);

    Mono<Void> deregisterEmployee(String customerId, String employeeId);

}
