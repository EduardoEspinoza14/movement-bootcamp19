package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    //PARA OBTENER TODOS LOS PRODUCTOS POR ID CLIENTE
    Flux<Product> getProductsByCustomer(String customerId);

    //PARA OBTNER EL PRODUCTO POR ID CLIENTE Y ID PRODUCTO, ESTO VALIDA LA PERTENENCIA Y EXISTENCIA
    Mono<Product> getProductByCustomerAndId(String customerId, String productId);

    //PARA INSERTAR UN PRODUCTO INVOCANDO A LA API
    Mono<Product> insertProduct(Product product);

    //PARA VALIDAR SI EL CLIENTE PUEDE ACCEDER A UN PRODUCTO EN ESPECIFICO, SIGUIENDO LAS REGLAS DEL NEGOCIO
    Mono<Product> validateCustomerCanProduct(MovementDto movementDto);

    //PARA OBTENER EL SALDO EN CUENTA SI ES EL PRODUCTO DE TIPO CUENTA, Y EL SALDO DE CREDITO DISPONIBLE, SI ES PRODUCTO DEL TIPO CREDITO
    Mono<Double> getAvailableBalance(String customerId, String productId);

}
