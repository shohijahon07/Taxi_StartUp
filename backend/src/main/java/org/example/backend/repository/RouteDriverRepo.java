package org.example.backend.repository;


import org.example.backend.entity.Route_Driver;
import org.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteDriverRepo extends JpaRepository<Route_Driver, UUID> {


    Route_Driver findByUser(Optional<User> user);
   List<Route_Driver> findAllByUser(User user);
    List<Route_Driver> findByFromCityAndToCity(String fromCity, String toCity);

    List<Route_Driver> findAllByFromCityAndToCity(String fromCity, String toCity);



}
