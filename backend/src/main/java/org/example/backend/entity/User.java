package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid default gen_random_uuid()")
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String password;
    private String chatID;
    private String carType;
    private String carImg;
    private String driverImg;
    private String cardDocument;
    private String about;

    private Boolean isDriver=false;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    public User(String fullName, String phoneNumber, String password, String chatID, String carType, String carImg, String driverImg, String cardDocument, Boolean isDriver, Status status, List<Role> roles) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.chatID = chatID;
        this.carType = carType;
        this.carImg = carImg;
        this.driverImg = driverImg;
        this.cardDocument = cardDocument;
        this.isDriver = isDriver;
        this.status = status;
        this.roles = roles;
    }

    public User(UUID id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return fullName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
