package org.example.backend.service.driver;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.DriverDto;
import org.example.backend.DTO.IsDriving;
import org.example.backend.DTO.UserDto;
import org.example.backend.entity.Role;
import org.example.backend.entity.Status;
import org.example.backend.entity.User;
import org.example.backend.repository.RoleRepo;
import org.example.backend.repository.RouteDriverRepo;
import org.example.backend.repository.UserRepo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserImpl implements UserService{
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final RouteDriverRepo routeDriverRepo;

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
        try {
            User user = userRepo.findById(id).orElseThrow();

            // Parolni encode qilmasdan oldingi original holati
            String plainPassword = isDriving.getPassword();

            // Parolni encode qilish
            user.setPassword(passwordEncoder.encode(plainPassword));
            user.setIsDriver(isDriving.getIsDriver());
            user.setStatus(Status.START);

            userRepo.save(user);

            String apiToken ="7170837425:AAGYpViG20xIwtYVNacL7jW47pjxoWFWJc0";
            String chatId = String.valueOf(user.getChatId());
            String text = "Tabriklaymiz siz muvaffaqiyatli tastiqlandingiz Sizning parolingiz: " + plainPassword + "va username: " +user.getPhoneNumber();

            String urlString = "https://api.telegram.org/bot" + apiToken + "/sendMessage?chat_id=" + chatId + "&text=" + text;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            System.out.println(content.toString());

            return ResponseEntity.ok("edit Successful");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message");
        }
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

    @Override
    @Transactional
    public ResponseEntity<?> deleteUser(UUID id) {
        routeDriverRepo.deleteByUserId(id);

        userRepo.deleteById(id);

        return ResponseEntity.ok("delete successfull");
    }
    @Override
    public void saveUser(UserDto userDto) {
        System.out.println(userDto);
        System.out.println(userDto.getCardDocument());

        List<Role> roles = new ArrayList<>();
        Role driverRole = roleRepo.findByName("ROLE_DRIVER");
        if (driverRole == null) {
            driverRole = new Role("ROLE_DRIVER");
            roleRepo.save(driverRole);
        }
        roles.add(driverRole);

        Optional<User> byChatId = userRepo.findByChatId(userDto.getChatId());

        if (byChatId.isPresent()) {
            User user1 = byChatId.get();
            user1.setFullName(userDto.getFullName());
            user1.setPhoneNumber(userDto.getPhoneNumber());

            user1.setCarType(userDto.getCarType());
            user1.setCarImg(userDto.getCarImg());
            user1.setDriverImg(userDto.getDriverImg());
            user1.setCardDocument(userDto.getCardDocument());
            user1.setAbout(userDto.getAbout());
            user1.setChatId(userDto.getChatId());
            user1.setRoles(roles);

            userRepo.save(user1);
        } else {
            User newUser = new User();
            newUser.setFullName(userDto.getFullName());
            newUser.setPhoneNumber(userDto.getPhoneNumber());
            newUser.setCarType(userDto.getCarType());
            newUser.setCarImg(userDto.getCarImg());
            newUser.setDriverImg(userDto.getDriverImg());
            newUser.setCardDocument(userDto.getCardDocument());
            newUser.setAbout(userDto.getAbout());
            newUser.setChatId(userDto.getChatId());
            newUser.setRoles(roles);

            userRepo.save(newUser);
        }
    }

}






