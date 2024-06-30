package com.app.backend.business.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.app.backend.business.dto.DBUserDto;
import com.app.backend.business.dto.SenderDto;
import com.app.backend.data.entity.ChatEntity;
import com.app.backend.data.entity.UserEntity;

@Service
public class CommonService {
    public UserEntity dbUserDtoToEntity(DBUserDto dbRequestDto) {
        if (dbRequestDto == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(dbRequestDto.getEmail());
        userEntity.setFirstName(dbRequestDto.getFirstName());
        userEntity.setLastName(dbRequestDto.getLastName());
        userEntity.setBirthday(dbRequestDto.getBirthday());
        userEntity.setSalary(dbRequestDto.getSalary());

        return userEntity;
    }

    public DBUserDto entityToDBUserDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        return DBUserDto.builder()
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .birthday(userEntity.getBirthday())
                .salary(userEntity.getSalary())
                .build();
    }

    public List<UserEntity> dbUserDtosToEntities(List<DBUserDto> dbRequestDtos) {
        if (dbRequestDtos == null) {
            return null;
        }

        return dbRequestDtos.stream()
                .map(this::dbUserDtoToEntity)
                .collect(Collectors.toList());
    }

    public List<DBUserDto> entitiesToDBUserDtos(List<UserEntity> userEntities) {
        if (userEntities == null) {
            return null;
        }

        return userEntities.stream()
                .map(this::entityToDBUserDto)
                .collect(Collectors.toList());
    }

    public ChatEntity senderDtoToEntity(SenderDto senderDto) {
        if (senderDto == null) {
            return null;
        }

        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setMessage(senderDto.getMessage());
        chatEntity.setSender(senderDto.getSender());

        return chatEntity;
    }
}
