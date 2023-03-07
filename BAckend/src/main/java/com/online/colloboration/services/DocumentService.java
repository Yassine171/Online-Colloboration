package com.online.colloboration.services;

import com.online.colloboration.dto.DocumentDto;
import com.online.colloboration.mapper.DocumentMapper;
import com.online.colloboration.models.Document;
import com.online.colloboration.repository.DocumentRepository;
import com.online.colloboration.repository.UserRepository;
import com.online.colloboration.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentMapper documentMapper;


    public DocumentDto uploadDocument(MultipartFile file, String name, String username) throws IOException {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Document document = new Document();
        document.setName(name);
        document.setFilePath("demo/BAckend/uploads/" + fileName);
        document.setUploadDate(LocalDateTime.now());
        document.setUser(userRepository.findByEmail(username).orElse(null));
        documentRepository.save(document);

        String uploadDir = "demo/BAckend/uploads/";
        FileUploadUtil.saveFile(uploadDir, fileName, file);

        return documentMapper.toDTO(document);
    }

    public DocumentDto getDocumentById(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        return documentMapper.toDTO(document);
    }

    public List<DocumentDto> getAllDocuments() {

        List<Document> documentList = documentRepository.findAll();

        return documentMapper.toDtoList(documentList);
    }
}
