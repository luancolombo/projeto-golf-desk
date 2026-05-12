package com.project.golfofficeapi.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "rental_item")
public class RentalItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, length = 50)
    private String type;
    @Column(name = "total_stock", nullable = false)
    private Integer totalStock;
    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;
    @Column(name = "rental_price", nullable = false)
    private BigDecimal rentalPrice;
    @Column(nullable = false)
    private Boolean active;

    public RentalItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(BigDecimal rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RentalItem that = (RentalItem) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(),
                that.getName()) && Objects.equals(getType(), that.getType()) && Objects.equals(getTotalStock(),
                that.getTotalStock()) && Objects.equals(getAvailableStock(), that.getAvailableStock()) && Objects.equals(getRentalPrice(),
                that.getRentalPrice()) && Objects.equals(getActive(), that.getActive());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getType(), getTotalStock(), getAvailableStock(), getRentalPrice(), getActive());
    }
}
