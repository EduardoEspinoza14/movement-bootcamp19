package com.nttdata.movement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementDto {

    public MovementDto (Customer customer, Product product){
        this.customer = customer;
        this.product = product;
    }

    private String id;
    private String type;
    private Product product;
    private Customer customer;

}
