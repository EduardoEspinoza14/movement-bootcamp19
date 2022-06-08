package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.EmployeeService;
import com.nttdata.movement.model.dto.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Value("${api.customer.employeeUri}")
    private String baseUri;

    @Value("${api.customer.signerUri}")
    private String personUri;

    @Value("${api.customer.holderUri}")
    private String companyUri;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Override
    public Flux<Employee> listEmployees(String customerId) {
        return null;
    }

    @Override
    public Mono<Employee> registerSigner(String customerId, Employee employee) {
        return null;
    }

    @Override
    public Mono<Employee> registerHolder(String customerId, Employee employee) {
        return null;
    }

    @Override
    public Mono<Employee> updateEmployee(String customerId, Employee employee) {
        return null;
    }

    @Override
    public Mono<Void> deregisterEmployee(String customerId, String employeeId) {
        return null;
    }
}
