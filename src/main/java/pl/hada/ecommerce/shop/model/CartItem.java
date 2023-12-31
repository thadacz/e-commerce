package pl.hada.ecommerce.shop.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne private Product product;
  private Integer quantity;
  private Integer stock;

  public CartItem(Product product, Integer quantity) {
    this.product = product;
    this.quantity = quantity;
    this.stock = product.getStock();
  }
}
