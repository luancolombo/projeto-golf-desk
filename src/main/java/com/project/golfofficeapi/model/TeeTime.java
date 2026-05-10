package com.project.golfofficeapi.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
        name = "tee_time",
        uniqueConstraints = @UniqueConstraint(columnNames = {"play_date", "start_time"}))
public class TeeTime implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers = 4;
    @Column(name = "booked_players", nullable = false)
    private Integer bookedPlayers;
    @Column(nullable = false, length = 30)
    private String status;
    @Column(name = "base_green_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseGreenFee;

    public TeeTime() {}

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
        TeeTime teeTime = (TeeTime) o;
        return Objects.equals(getId(), teeTime.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
