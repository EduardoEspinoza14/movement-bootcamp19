package com.nttdata.movement.model.dto;

import com.nttdata.movement.model.mongo.MovementMongo;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class MovementDto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementDto {

  /**
   * Method transformIntoMongo.
   */
  public static MovementMongo transformIntoMongo(MovementDto dto) {
    MovementMongo mongo = new MovementMongo();
    mongo.setId(dto.getId());
    mongo.setConcept(dto.getConcept());
    mongo.setDate(dto.getDate());
    mongo.setType(dto.getType());
    mongo.setAmount(dto.getAmount());
    mongo.setCustomerId(dto.getCustomer().getId());
    mongo.setProductId(dto.getProduct().getId());
    return mongo;
  }

  /**
   * Method transformIntoDto.
   */
  public static MovementDto transformIntoDto(MovementMongo mongo) {
    MovementDto dto = new MovementDto();
    dto.setId(mongo.getId());
    dto.setConcept(mongo.getConcept());
    dto.setDate(mongo.getDate());
    dto.setType(mongo.getType());
    dto.setAmount(mongo.getAmount());
    dto.setCustomer(new Customer(mongo.getCustomerId()));
    dto.setProduct(new Product(mongo.getProductId()));
    return dto;
  }

  public MovementDto(Customer customer, Product product) {
    this.customer = customer;
    this.product = product;
  }

  private String id;
  private String concept;
  private Date date;
  private Double amount;
  private String type;
  private Product product;
  private Customer customer;

}
