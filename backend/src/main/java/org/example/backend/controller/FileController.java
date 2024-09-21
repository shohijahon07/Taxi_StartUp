package org.example.backend.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
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
        FileInputStream inputStream = new FileInputStream("files/" + img);
        ServletOutputStream outputStream = response.getOutputStream();
        inputStream.transferTo(outputStream);
        inputStream.close();
        outputStream.close();
    }


    @PostMapping("/photo")
    public String saveFile(@RequestParam MultipartFile file) throws IOException {
        // Noyob fayl nomini yaratilmoqda
        String img = UUID.randomUUID() + file.getOriginalFilename();
        File outputFile = new File("files/" + img);

        // Asl fayl o'lchamini olish
        long originalSize = file.getSize();

        // Thumbnails yordamida rasmni 2 barobar kichik qilib saqlash
        Thumbnails.of(file.getInputStream())
                .scale(0.5)  // Rasmni o'lchamlarini 2 barobar kichraytiradi
                .outputQuality(0.8) // Sifatni sozlaydi
                .toFile(outputFile);

        // Rasmning yangi kichik o'lchami
        long compressedSize = outputFile.length();
        System.out.println("orginal"+originalSize);
        System.out.println("qisqardi"+compressedSize);

        return img;
    }








}
