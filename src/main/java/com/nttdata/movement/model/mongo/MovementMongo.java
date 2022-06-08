package com.nttdata.movement.model.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "movements")
public class MovementMongo {

    public static String MOVEMENT_TYPE_1 = "Income";
    public static String MOVEMENT_TYPE_2 = "Expenses";

    public static String MOVEMENT_CONCEPT_1 = "Account Opening";

    @Id
    private String id;

    private String concept;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date date;
    private String type;
    private Double amount;
    private String customerId;
    private String productId;

}
