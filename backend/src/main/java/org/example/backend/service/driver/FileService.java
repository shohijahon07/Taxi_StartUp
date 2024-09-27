package org.example.backend.service.driver;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final String UPLOAD_DIR = "backend/file/";

    public String saveFile(MultipartFile file) throws IOException {
        // Create a unique file name
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Ensure the directory exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file to the local file system
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return fileName;  // Return the file name or path for saving in the database
    }
}
