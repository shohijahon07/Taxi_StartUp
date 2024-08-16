package org.example.backend.repository;
import org.example.backend.entity.Role;
import org.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneNumber(String fullName);
    Optional<User> findAllByFullName(String fullName);


    List<User> findAllById(UUID id);


    Integer countAllByRoles(List<Role> roles);
    List<User> findAllByRolesAndIsDriver(List<Role> roles, Boolean isDriver);


}
