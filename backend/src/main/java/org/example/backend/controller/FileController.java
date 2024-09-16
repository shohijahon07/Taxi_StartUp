package org.example.backend.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@RestController
    @RequestMapping("/api/fileController")
@CrossOrigin
public class FileController {
    @GetMapping("/photo")
    public void getFile(@RequestParam String img, HttpServletResponse response) throws IOException {
        FileInputStream inputStream = new FileInputStream("backend/files/" + img);
        ServletOutputStream outputStream = response.getOutputStream();
        inputStream.transferTo(outputStream);
        inputStream.close();
        outputStream.close();
    }

    @PostMapping("/photo")
    public String saveFile(@RequestParam MultipartFile file) throws IOException {
        String img = UUID.randomUUID() + file.getOriginalFilename();
        FileOutputStream outputStream = new FileOutputStream("backend/files/" + img);
        outputStream.write(file.getBytes());
        outputStream.close();
        return img;
    }






}
