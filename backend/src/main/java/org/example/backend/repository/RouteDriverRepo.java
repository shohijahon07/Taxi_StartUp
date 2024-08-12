package org.example.backend.repository;


import org.example.backend.entity.Role;
import org.example.backend.entity.Route_Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RouteDriverRepo extends JpaRepository<Route_Driver, UUID> {

}
