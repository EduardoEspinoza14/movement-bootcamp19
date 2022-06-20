package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface MovementService.
 */
public interface MovementService {

  //PARA LISTAR LOS MOVIMIENTOS DE UN PRODUCTO POR ID DE PRODUCTO Y CLIENTE
  Flux<MovementDto> listMovements(String customerId, String productId);

  //PARA REGISTRAR EL PRIMER MOVIMIENTO DE UN PRODUCTO,
  // ESTE MOVIMIENTO DE APERTURA TAMBIEN REGISTRA EL PRODUCTO Y EL CLIENTE DE SER NECESARIO
  Mono<MovementDto> accountOpening(MovementDto movementDto);

  //PARA REGISTRAR UN MOVIMIENTO DE RETIRO SOBRE LOS PRODUCTOS DE TIPO CUENTAS
  Mono<MovementDto> registerWithdrawal(MovementDto movementDto);

  //PARA REGISTRAR UN MOVIMIENTO DE DEPOSITO SOBRE LOS PRODUCTOS DE TIPO CUENTAS
  Mono<MovementDto> registerDeposit(MovementDto movementDto);

  //PARA REGISTRAR UN PAGO SOBRE UN PRODUCTO DE TIPO CREDITO
  Mono<MovementDto> registerPayment(MovementDto movementDto);

  //PARA REGISTRAR UN GASTO O COSUMO SOBRE UN PRODUCTO DE TIPO CREDITO
  Mono<MovementDto> registerSpent(MovementDto movementDto);

}
