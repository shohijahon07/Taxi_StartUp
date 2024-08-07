package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String chatID;
    private String carType;
    private String carImg;
    private String driverImg;
    private String cardDocument;
    @Enumerated(EnumType.STRING)
    private Status status;
}
