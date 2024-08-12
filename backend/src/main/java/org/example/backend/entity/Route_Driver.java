package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "routes")
public class Route_Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;
    private String fromCity;
    private String toCity;
    private Integer countSide;
    private Integer price;
    private LocalDate day;
    private Time hour;
    @ManyToOne
    private User user;

    public Route_Driver(String fromCity, String toCity, Integer countSide, Integer price, LocalDate day, Time hour, User user) {
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.countSide = countSide;
        this.price = price;
        this.day = day;
        this.hour = hour;
        this.user = user;
    }
}
