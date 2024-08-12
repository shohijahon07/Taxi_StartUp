package org.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDriverDto {
    private String fromCity;
    private String toCity;
    private Integer countSide;
    private Integer price;
    private LocalDateTime day;
    private LocalDateTime hour;
    private String userId;

}
