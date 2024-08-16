package org.example.backend.service.driver;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.DriverDto;
import org.example.backend.DTO.IsDriving;
import org.example.backend.entity.Role;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepo;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class UserImpl implements UserService{
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    @Override
    public ResponseEntity<?> getDriverOne(UUID id) {
        List<User> users = userRepo.findAllById(id);
        return ResponseEntity.ok(users);
    }

    @Override
    public HttpEntity<?> saveAboutDriver(UUID id, String driverAboutDto) {
        User user = userRepo.findById(id).orElseThrow();
        user.setAbout(driverAboutDto);
        userRepo.save(user);
        return null;
    }

    @Override
    public HttpEntity<?> editDriver(UUID id, DriverDto driverDto) {

        User user = userRepo.findById(id).orElseThrow();
        user.setDriverImg(driverDto.getDriverImg());
        user.setAbout(driverDto.getAbout());
        user.setCardDocument(driverDto.getCardDocument());
        user.setCarImg(driverDto.getCarImg());
        user.setCarType(driverDto.getCarType());
        user.setFullName(driverDto.getFullName());
        user.setPhoneNumber(driverDto.getPhoneNumber());
        userRepo.save(user);
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<?> getDriversAll(List<Role> roleDriver, Boolean isDriver) {
        List<User> all = userRepo.findAllByRolesAndIsDriver(roleDriver,isDriver);
        return ResponseEntity.ok(all);
    }

    @Override
    public HttpEntity<?> editDriverIsDriving(UUID id, IsDriving isDriving) {
        User user = userRepo.findById(id).orElseThrow();
        user.setIsDriver(isDriving.getIsDriver());
        user.setPassword(passwordEncoder.encode(isDriving.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("edit Successfull");
    }

    @Override
    public ResponseEntity<?> countDriver(List<Role> roleDriver) {
        Integer count = userRepo.countAllByRoles(roleDriver);
        return ResponseEntity.ok(count);
    }

    @Override
    public ResponseEntity<?> CountUserAll(List<Role> roleDriver) {
        Integer users = userRepo.countAllByRoles(roleDriver);
        return ResponseEntity.ok(users);
    }
}
