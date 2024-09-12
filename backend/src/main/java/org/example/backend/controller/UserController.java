package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.DriverDto;
import org.example.backend.DTO.IsDriving;
import org.example.backend.DTO.PessengerDto;
import org.example.backend.DTO.UserDto;
import org.example.backend.entity.Role;
import org.example.backend.repository.RoleRepo;
import org.example.backend.service.driver.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
        private final UserService userService;
        private final RoleRepo roleRepo;
    @GetMapping
    public ResponseEntity<?> getDriverOne(@RequestParam UUID id){
        return userService.getDriverOne(id);
    }
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> SearchDriver(@RequestParam String name){
        return userService.SearchNameDriver(name);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    public ResponseEntity<?> DeleteUser(@RequestParam UUID id){
        return userService.deleteUser(id);
    }

    @PostMapping("/pessenger")
    public HttpEntity<?> saveUser(@RequestBody PessengerDto pessengerDto) throws IOException {
        System.out.println(pessengerDto);
        return userService.savePessenger(pessengerDto);
    }
   @PostMapping("/save")
   public ResponseEntity<?>save(@RequestBody UserDto userDto){
       return ResponseEntity.ok(userService.saveUser(userDto));
}
    @GetMapping("/drivers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getDriverAll(@RequestParam Boolean isDriver){
        List<Role> roleDriver = roleRepo.findAllByName("ROLE_DRIVER");
        return userService.getDriversAll(roleDriver,isDriver);
    }
    @GetMapping("/countD")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> CountDriver(){
        List<Role> roleDriver = roleRepo.findAllByName("ROLE_DRIVER");
        return userService.countDriver(roleDriver);
    }
    @GetMapping("/countU")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> CountUser(){
        List<Role> roleUser = roleRepo.findAllByName("ROLE_USER");
        return userService.CountUserAll(roleUser);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    public HttpEntity<?> saveAboutDriverAbout(@RequestParam UUID id, @RequestParam String text){
        return userService.saveAboutDriver(id,text);
    }
    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    public HttpEntity<?> EditAboutDriver(@RequestParam UUID id, @RequestBody DriverDto driverDto){
        return userService.editDriver(id,driverDto);
    }

    @PutMapping("/isDrive")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    public HttpEntity<?> EditIsDriver(@RequestParam UUID id, @RequestBody IsDriving isDriving) throws IOException {
        return userService.editDriverIsDriving(id, isDriving);
    }
}
