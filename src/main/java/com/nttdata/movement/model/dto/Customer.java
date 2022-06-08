package com.nttdata.movement.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    public static String CUSTOMER_TYPE_1 = "Person";
    public static String CUSTOMER_TYPE_2 = "Company";

    public Customer(String id){
        this.id = id;
    }

    private String id;
    private String name;
    private String last_name;
    private Date date_born;
    private String address;
    private String phone;
    private String type;
    private String RUC;
    private String DNI;
    private List<Product> products;

}
