package com.app.backend.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.business.dto.CVDto;
import com.app.backend.data.entity.CVEntity;
import com.app.backend.data.repository.CVRepository;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/cv")
public class CVController {

    @Autowired
    private CVRepository cvRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCV(@Valid @ModelAttribute CVDto cvDto) {
        CVEntity currentCV = cvRepository.findByUserEmail(cvDto.getUserEmail());

        if (currentCV == null) {
            try {
                CVEntity cv = new CVEntity();
                cv.setName(cvDto.getName());

                String currentPath = new java.io.File(".").getCanonicalPath();
                String fileName = "cv_" + cvDto.getUserEmail() + System.currentTimeMillis() + ".pdf";
                String filePath = currentPath + "/frontend/public/cvs/" + fileName;
                Files.write(Paths.get(filePath), cvDto.getFile().getBytes());

                cv.setFilePath("/cvs/" + fileName);

                cv.setFileType(cvDto.getFile().getContentType());
                cv.setUserEmail(cvDto.getUserEmail());

                cvRepository.save(cv);
                return ResponseEntity.ok().body("CV uploaded successfully");
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("Failed to upload CV.");
            }

        } else {
            try {
                String currentPath = new java.io.File(".").getCanonicalPath();

                Files.delete(Paths.get(currentPath + "/frontend/public/" + currentCV.getFilePath()));

                currentCV.setName(cvDto.getName());

                String fileName = "cv_" + cvDto.getUserEmail() + System.currentTimeMillis() + ".pdf";
                String filePath = currentPath + "/frontend/public/cvs/" + fileName;

                Files.write(Paths.get(filePath), cvDto.getFile().getBytes());

                currentCV.setFilePath("/cvs/" + fileName);

                currentCV.setFileType(cvDto.getFile().getContentType());
                currentCV.setUserEmail(cvDto.getUserEmail());

                cvRepository.save(currentCV);
                return new ResponseEntity<>("CV updated successfully", HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>("Failed to update CV" + e,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/info/{userEmail}")
    public ResponseEntity<?> getCVByUserEmail(@PathVariable String userEmail) {
        Optional<CVEntity> cvEntityOptional = Optional.ofNullable(cvRepository.findByUserEmail(userEmail));
        if (cvEntityOptional.isPresent()) {
            CVEntity cvEntity = cvEntityOptional.get();
            return ResponseEntity.ok(cvEntity);
        } else {
            return ResponseEntity.status(404).body("CV not found for user email: " + userEmail);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listCVs() throws IOException {
        String currentPath = new java.io.File(".").getCanonicalPath();
        String filesPath = currentPath + "/frontend/public/cvs/";

        File folder = new File(filesPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        List<String> fileNames = new ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        return ResponseEntity.ok(fileNames);
    }

}
