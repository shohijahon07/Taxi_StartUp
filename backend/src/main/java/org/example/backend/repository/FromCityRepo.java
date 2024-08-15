package org.example.backend.repository;

import org.example.backend.entity.FromCity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FromCityRepo extends JpaRepository<FromCity, UUID> {
}
