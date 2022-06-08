package com.nttdata.movement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    public static String EMPLOYEE_TYPE_1 = "Holder";
    public static String EMPLOYEE_TYPE_2 = "Signer";

    public Employee(String id){
        this.id = id;
    }

    private String id;
    private String name;
    private String last_name;
    private String type;
    private Customer company;


}
