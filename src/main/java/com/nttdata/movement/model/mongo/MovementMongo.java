package com.nttdata.movement.model.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Class MovementMongo.
 */
@Data
@Document(collection = "movements")
public class MovementMongo {

  public static String MOVEMENT_TYPE_1 = "Income";
  public static String MOVEMENT_TYPE_2 = "Expenses";

  public static String MOVEMENT_CONCEPT_1 = "Account Opening";
  public static String MOVEMENT_CONCEPT_2 = "Withdrawal Account";
  public static String MOVEMENT_CONCEPT_3 = "Deposit Account";
  public static String MOVEMENT_CONCEPT_4 = "Payment Credit";
  public static String MOVEMENT_CONCEPT_5 = "Spent Credit";

  @Id
  private String id;

  private String concept;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date date;
  private String type;
  private Double amount;
  private String customerId;
  private String productId;

}
