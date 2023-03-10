package com.online.colloboration.controller;

import com.online.colloboration.dto.DocumentDto;
import com.online.colloboration.models.Doc;
import com.online.colloboration.models.User;
import com.online.colloboration.repository.UserRepository;
import com.online.colloboration.services.DocService;
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
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocService docService;
    private final UserRepository userRepository;


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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentById(@PathVariable("id") Long id) {
        documentService.deleteDocumentById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDto> updateDocument(@PathVariable("id") Long id,
                                                      @RequestParam String name,@RequestParam("file") MultipartFile file) throws IOException {
        DocumentDto updatedDocumentDto = documentService.updateDocument(id, name,file);
        return ResponseEntity.ok(updatedDocumentDto);
    }

    @PostMapping("/new")
    public ResponseEntity<Doc> createDoc(@RequestBody Doc doc) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser=userRepository.findByEmail(username).orElse(null);
        doc.setOwner(currentUser);

        // Save the new document and return it in the response
        Doc savedDoc = docService.createDoc(doc);
        return ResponseEntity.ok(savedDoc);
    }
}
