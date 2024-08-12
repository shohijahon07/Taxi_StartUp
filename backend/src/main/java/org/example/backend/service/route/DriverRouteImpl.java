package org.example.backend.service.route;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.RouteDriverDto;
import org.example.backend.repository.RouteDriverRepo;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
@RequiredArgsConstructor
@Service
public class DriverRouteImpl implements DriverRouteService{
private final RouteDriverRepo routeDriverRepo;
    @Override
    public ResponseEntity<?> getRoute() {

        return null;
    }

    @Override
    public HttpEntity<?> saveRoute(RouteDriverDto routeDriverDto) {
        return null;
    }

    @Override
    public HttpEntity<?> EditRoute(UUID id, RouteDriverDto routeDriverDto) {
        return null;
    }

    @Override
    public HttpEntity<?> DeleteRoute(UUID id) {
        return null;
    }
}
