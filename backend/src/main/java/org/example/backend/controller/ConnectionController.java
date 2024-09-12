package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.ConnectionDto;
import org.example.backend.service.connection.ConnectionService;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/connection")
@RequiredArgsConstructor
public class ConnectionController {
    private ConnectionService connectionService;

    @PostMapping()
//    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_MENTOR')")
    public HttpEntity<?> saveUser(@RequestParam Integer language,@RequestBody ConnectionDto connectionDto) {
        System.out.println(connectionDto);
        return connectionService.saveConnection(language,connectionDto);
    }
@GetMapping
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public  HttpEntity<?> getConnection(@RequestParam Integer language){
      return   connectionService.getConnection(language);
}
}
