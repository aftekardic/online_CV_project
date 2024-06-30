package com.app.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.business.dto.SenderDto;
import com.app.backend.business.service.CommonService;
import com.app.backend.data.repository.ChatRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    CommonService commonService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllChat() {
        return ResponseEntity.ok().body(chatRepository.findAll());
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SenderDto senderDto) {
        chatRepository.save(commonService.senderDtoToEntity(senderDto));
        return null;
    }

}
