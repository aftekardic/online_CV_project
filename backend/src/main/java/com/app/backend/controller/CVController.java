package com.app.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.business.dto.CVDto;
import com.app.backend.data.entity.CVEntity;
import com.app.backend.data.repository.CVRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cv")
public class CVController {

    @Autowired
    private CVRepository cvRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCV(@Valid @ModelAttribute CVDto cvDto) {
        CVEntity currentCV = cvRepository.findByUserEmail(cvDto.getUserEmail());
        if (currentCV == null) {
            try {
                CVEntity cv = new CVEntity();
                cv.setName(cvDto.getName());
                cv.setFileData(cvDto.getFile().getBytes());
                cv.setFileType(cvDto.getFile().getContentType());
                cv.setUserEmail(cvDto.getUserEmail());

                cvRepository.save(cv);
                return new ResponseEntity<>("CV uploaded successfully", HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>("Failed to upload CV",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            try {
                currentCV.setName(cvDto.getName());
                currentCV.setFileData(cvDto.getFile().getBytes());
                currentCV.setFileType(cvDto.getFile().getContentType());
                currentCV.setUserEmail(cvDto.getUserEmail());

                cvRepository.save(currentCV);
                return new ResponseEntity<>("CV updated successfully", HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>("Failed to update CV",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

}
