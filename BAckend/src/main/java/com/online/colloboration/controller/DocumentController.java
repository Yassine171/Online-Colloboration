package com.online.colloboration.controller;

import com.online.colloboration.dto.DocumentDto;
import com.online.colloboration.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.IOException;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;


    @PostMapping
    public ResponseEntity<DocumentDto> uploadDocument(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("name") String name) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        DocumentDto documentDto = documentService.uploadDocument(file, name, username);

        return ResponseEntity.ok(documentDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable("id") Long id) {

        DocumentDto documentDto = documentService.getDocumentById(id);

        return ResponseEntity.ok(documentDto);
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {

        List<DocumentDto> documentDtoList = documentService.getAllDocuments();

        return ResponseEntity.ok(documentDtoList);
    }
}
