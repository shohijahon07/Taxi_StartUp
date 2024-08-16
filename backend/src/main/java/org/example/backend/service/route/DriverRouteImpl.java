package org.example.backend.service.route;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.RouteDriverDto;
import org.example.backend.entity.Route_Driver;
import org.example.backend.entity.User;
import org.example.backend.repository.RouteDriverRepo;
import org.example.backend.repository.UserRepo;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class DriverRouteImpl implements DriverRouteService{
private final RouteDriverRepo routeDriverRepo;
private final UserRepo userRepo;

    @Override
    public ResponseEntity<?> getRoute() {
        List<Route_Driver> drivers = routeDriverRepo.findAll();
        return ResponseEntity.ok(drivers);
    }

    @Override
    public HttpEntity<?> saveRoute(RouteDriverDto routeDriverDto) {
        System.out.println(routeDriverDto);
        Route_Driver routeDriver = new Route_Driver(routeDriverDto.getFromCity(), routeDriverDto.getToCity(), routeDriverDto.getCountSide(), routeDriverDto.getPrice(), routeDriverDto.getDay(), routeDriverDto.getHour(), new User(routeDriverDto.getUserId()));
        routeDriverRepo.save(routeDriver);

        return ResponseEntity.ok("save succesfull");
    }

    @Override
    public HttpEntity<?> EditRoute(UUID id, RouteDriverDto routeDriverDto) {
        Route_Driver routeDriver = routeDriverRepo.findById(id).orElseThrow();

        routeDriver.setCountSide(Integer.valueOf(routeDriverDto.getCountSide()));
        routeDriver.setDay(LocalDate.parse(routeDriverDto.getDay()));
        routeDriver.setHour(String.valueOf(routeDriverDto.getHour()));
        routeDriver.setPrice(Integer.valueOf(routeDriverDto.getPrice()));
        routeDriver.setFromCity(routeDriverDto.getFromCity());
        routeDriver.setToCity(routeDriverDto.getToCity());

        routeDriverRepo.save(routeDriver);
        return ResponseEntity.ok("edit Successfull");
    }

    @Override
    public HttpEntity<?> DeleteRoute(UUID id) {
        routeDriverRepo.deleteById(id);
        return ResponseEntity.ok("edit succesful");
    }

    @Override
    public ResponseEntity<?> getRouteByDriver(UUID id) {
        User user = userRepo.findById(id).orElseThrow();
        List<Route_Driver> drivers = routeDriverRepo.findAllByUser(user);

        return ResponseEntity.ok(drivers);
    }
}
