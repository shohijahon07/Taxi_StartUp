package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.FromCityDto;
import org.example.backend.DTO.ToCityDto;
import org.example.backend.service.fromCity.FromCityService;
import org.example.backend.service.toCity.ToCityService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/toCity")
@RequiredArgsConstructor
public class ToCityController {
    private final ToCityService toCityService;

    @GetMapping
    public ResponseEntity<?> getRoute(){
        return toCityService.getCity();
    }
    @PostMapping
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_MENTOR')")
    public HttpEntity<?> saveCity( @RequestBody ToCityDto toCityDto){
        return toCityService.saveCity(toCityDto);
    }
    @PutMapping
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_MENTOR')")
    public HttpEntity<?> EditCity( @RequestParam UUID id, @RequestBody ToCityDto toCityDto){
        return toCityService.EditCity(id,toCityDto);
    }
    @DeleteMapping
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_MENTOR')")
    public HttpEntity<?> DeleteRoute(@RequestParam UUID id){
        return toCityService.DeleteRoute( id);
    }
}
