package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.RouteDriverDto;
import org.example.backend.service.route.DriverRouteImpl;
import org.example.backend.service.route.DriverRouteService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverRouteService driverRouteService;

    @GetMapping
    public ResponseEntity<?> checkUserRoles(){
        return driverRouteService.getRoute();
    }
    @PostMapping
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_MENTOR')")
    public HttpEntity<?> saveRoute( @RequestBody RouteDriverDto routeDriverDto){
        return driverRouteService.saveRoute(routeDriverDto);
    }
    @PutMapping
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_MENTOR')")
    public HttpEntity<?> EditRoute( @RequestParam UUID id, @RequestBody RouteDriverDto routeDriverDto){
        return driverRouteService.EditRoute( id,routeDriverDto);
    }
    @DeleteMapping
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_MENTOR')")
    public HttpEntity<?> DeleteRoute(@RequestParam UUID id){
        return driverRouteService.DeleteRoute( id);
    }
}
