package com.nttdata.movement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    public static String PRODUCT_TYPE_1 = "Savings Account";
    public static String PRODUCT_TYPE_2 = "Checking Account";
    public static String PRODUCT_TYPE_3 = "Fixed Term";
    public static String PRODUCT_TYPE_4 = "Card";
    public static String PRODUCT_TYPE_5 = "Loan";

    public Product(String id){
        this.id = id;
    }

    private String id;
    private Date start_date;
    private String number;
    private String type;
    private Double credit_limit;
    private Date expiration_date;
    private String security_code;
    private Double commission_amount;
    private Integer single_day_movement;
    private Double credit_amount;
    private Integer payment_day;
    private Integer max_movement_limit;
    private String customerId;

}
