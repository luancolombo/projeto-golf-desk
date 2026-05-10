package com.project.golfofficeapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class TeeTimeDTO extends RepresentationModel<TeeTimeDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotNull(message = "Play date is Required!")
    private LocalDate playDate;
    @NotNull(message = "Start time is Required!")
    private LocalTime startTime;
    @NotNull(message = "Max players is Required!")
    @Min(value = 1, message = "Max players must be at least 1")
    private Integer maxPlayers;
    @NotNull(message = "Booked players is Required!")
    @Min(value = 0, message = "Booked players cannot be negative")
    private Integer bookedPlayers;
    @NotBlank(message = "Status is Required!")
    private String status;
    @NotNull(message = "Base green fee is Required!")
    @DecimalMin(value = "0.00", message = "Base green fee cannot be negative")
    private BigDecimal baseGreenFee;

    public TeeTimeDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPlayDate() {
        return playDate;
    }

    public void setPlayDate(LocalDate playDate) {
        this.playDate = playDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getBookedPlayers() {
        return bookedPlayers;
    }

    public void setBookedPlayers(Integer bookedPlayers) {
        this.bookedPlayers = bookedPlayers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBaseGreenFee() {
        return baseGreenFee;
    }

    public void setBaseGreenFee(BigDecimal baseGreenFee) {
        this.baseGreenFee = baseGreenFee;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TeeTimeDTO that = (TeeTimeDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
