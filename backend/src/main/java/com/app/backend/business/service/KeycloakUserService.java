package com.app.backend.business.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.backend.business.dto.DBUserDto;
import com.app.backend.business.dto.FormUserDto;
import com.app.backend.business.dto.UserDto;
import com.app.backend.data.entity.CVEntity;
import com.app.backend.data.entity.UserEntity;
import com.app.backend.data.repository.CVRepository;
import com.app.backend.data.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class KeycloakUserService {
        @Value("${keycloak.userinfo-url}")
        private String kcGetUserinfoUrl;

        @Value("${keycloak.update-url}")
        private String kcUpdateUrl;

        @Value("${keycloak.all-users-url}")
        private String kcAllUsersUrl;

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private CVRepository cvRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        CommonService commonService;

        private static final String AUTHORIZATION = "Authorization";

        public UserDto getUserInformationsByEmail(HttpServletRequest servletRequest) {
                String authorization = servletRequest.getHeader(AUTHORIZATION);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set(AUTHORIZATION, authorization);

                ResponseEntity<UserDto> response = restTemplate.postForEntity(kcGetUserinfoUrl,
                                new HttpEntity<>(headers), UserDto.class);

                return response.getBody();
        }

        @SuppressWarnings("unchecked")
        public Object getAllUsers(HttpServletRequest servletRequest) {

                String authorization = servletRequest.getHeader(AUTHORIZATION);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set(AUTHORIZATION, authorization);
                ResponseEntity<Object> allusersResponse = restTemplate.exchange(
                                kcAllUsersUrl,
                                HttpMethod.GET,
                                new HttpEntity<>(null, headers),
                                Object.class);
                ArrayList<Object> responseBody = (ArrayList<Object>) allusersResponse.getBody();

                List<Object> resultUsers = new ArrayList<>();

                if (responseBody == null || responseBody.isEmpty()) {
                        return resultUsers;
                }

                for (Object user : responseBody) {
                        if (user instanceof Map) {
                                Map<String, Object> userData = (Map<String, Object>) user;
                                String email = (String) userData.get("email");

                                UserEntity userEntity = userRepository.findByEmail(email);
                                CVEntity cvEntity = cvRepository.findByUserEmail(email);

                                if (userEntity != null) {

                                        userData.put("birthday", userEntity.getBirthday());
                                        userData.put("salary", userEntity.getSalary());

                                        if (cvEntity != null) {
                                                userData.put("uploadDate",
                                                                cvEntity.getUpdatedBy() != null
                                                                                ? cvEntity.getUpdatedBy()
                                                                                : cvEntity.getCreatedDate());
                                                userData.put("cvPath", cvEntity.getFilePath());
                                        } else {
                                                userData.put("uploadDate", null);
                                                userData.put("cvPath", null);
                                        }

                                } else {
                                        userData.put("birthday", null);
                                        userData.put("salary", null);

                                }

                                resultUsers.add(userData);
                        }
                }

                return resultUsers;
        }

        public DBUserDto getDBUserInformationsByEmail(String email) {
                return commonService.entityToDBUserDto(userRepository.findByEmail(email));
        }

        public ResponseEntity<?> updateUserInformations(FormUserDto formUserDto,
                        HttpServletRequest servletRequest) {
                String authorization = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set(HttpHeaders.AUTHORIZATION, authorization);

                try {
                        if (formUserDto.getPassword() != null) {

                                String dynamicJsonForPassword = String.format(
                                                "{\"type\":\"password\",\"temporary\":false,\"value\":\"%s\"}",
                                                formUserDto.getPassword());

                                restTemplate.exchange(
                                                kcUpdateUrl + "/" + formUserDto.getSub() + "/reset-password",
                                                HttpMethod.PUT,
                                                new HttpEntity<>(dynamicJsonForPassword,
                                                                headers),
                                                Object.class);
                        }

                        String dynamicJsonForUserInfo = String.format(
                                        "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\"}",
                                        formUserDto.getGiven_name(), formUserDto.getFamily_name(),
                                        formUserDto.getEmail());

                        restTemplate.exchange(
                                        kcUpdateUrl + "/" + formUserDto.getSub(),
                                        HttpMethod.PUT,
                                        new HttpEntity<>(dynamicJsonForUserInfo,
                                                        headers),
                                        Object.class);

                        String fileName = "cv_" + formUserDto.getEmail()
                                        + System.currentTimeMillis() + ".pdf";
                        CVEntity cv = cvRepository.findByUserEmail(formUserDto.getOld_email());
                        cv.setUserEmail(formUserDto.getEmail());
                        cv.setFilePath("/cvs/" + fileName);
                        cvRepository.save(cv);

                        String currentPath = new java.io.File(".").getCanonicalPath();
                        String filesPath = currentPath + "/frontend/public/cvs/";

                        File folder = new File(filesPath);
                        File[] listOfFiles = folder.listFiles(
                                        (dir, name) -> name.toLowerCase().contains(formUserDto.getOld_email()));

                        if (listOfFiles != null) {
                                for (File file : listOfFiles) {
                                        if (file.isFile()) {

                                                File newFile = new File(file.getParent(), fileName);
                                                file.renameTo(newFile);
                                        }
                                }
                        }
                        UserEntity existingUser = userRepository.findByEmail(formUserDto.getOld_email());

                        if (existingUser != null) {
                                DBUserDto dbUserDto = commonService.entityToDBUserDto(existingUser);
                                dbUserDto.setEmail(formUserDto.getEmail());
                                dbUserDto.setFirstName(formUserDto.getGiven_name());
                                dbUserDto.setLastName(formUserDto.getFamily_name());
                                dbUserDto.setBirthday(formUserDto.getBirthday());
                                dbUserDto.setSalary(formUserDto.getSalary());

                                UserEntity updatedUserEntity = commonService.dbUserDtoToEntity(dbUserDto);
                                updatedUserEntity.setId(existingUser.getId());

                                userRepository.save(updatedUserEntity);
                        } else {
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("There is an error when updating on DB... Contact to Admin");
                        }

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("There is an error when updating...");
                }

                return ResponseEntity.status(HttpStatus.OK)
                                .body("User updates successfully... Redirecting to sign page...");
        }

}
