package org.example.backend.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.LoginDto;
import org.example.backend.entity.User;
import org.example.backend.repository.RoleRepo;
import org.example.backend.repository.UserRepo;
import org.example.backend.service.jwt.JwtService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceIml implements AuthService{
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public HttpEntity<?> loginUser(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getFullName(),loginDto.getPassword())
        );
        User user = userRepo.findByFullName(loginDto.getFullName()).orElseThrow();
        System.out.println(user);
        Map<String, String> tokens = Map.of("access_token", jwtService.generateJwtToken(user),
                "refresh_token", jwtService.generateJwtRefreshToken(user),"fullName",user.getFullName());

        return ResponseEntity.ok(tokens);
    }

    @Override
    public HttpEntity<?> refreshTokenFunc(String refreshToken) {
        String id = jwtService.extractSubject(refreshToken);
        User user = userRepo.findById(UUID.fromString(id)).orElseThrow();
        return ResponseEntity.ok(jwtService.generateJwtToken(user));
    }

    @Override
    public ResponseEntity<?> checkUserRole(String authorization) {
        System.out.println(authorization);
            String id = jwtService.extractSubject(authorization);
            User user = userRepo.findById(UUID.fromString(id)).orElseThrow();
        System.out.println(user);
//            if (user!=null){
            return ResponseEntity.ok(user.getRoles());
//        }else {
//            return null;
//        }


    }
}
