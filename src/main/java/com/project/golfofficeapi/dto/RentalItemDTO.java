package com.project.golfofficeapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class RentalItemDTO extends RepresentationModel<RentalItemDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotBlank(message = "Name is Required!")
    private String name;
    @NotBlank(message = "Type is Required!")
    private String type;
    @NotNull(message = "Total stock is Required!")
    @Min(value = 0, message = "Total stock cannot be negative")
    private Integer totalStock;
    @Min(value = 0, message = "Available stock cannot be negative")
    private Integer availableStock;
    @NotNull(message = "Rental price is Required!")
    @DecimalMin(value = "0.00", message = "Rental price cannot be negative")
    private BigDecimal rentalPrice;
    private Boolean active;

    public RentalItemDTO() {}

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
        RentalItemDTO that = (RentalItemDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
