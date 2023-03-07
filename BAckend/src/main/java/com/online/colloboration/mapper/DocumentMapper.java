package com.online.colloboration.mapper;

import com.online.colloboration.dto.DocumentDto;
import com.online.colloboration.models.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentMapper {

    public DocumentDto toDTO(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setName(document.getName());
        dto.setFilePath(document.getFilePath());
        dto.setUploadDate(document.getUploadDate());
        dto.setUserId(document.getUser().getId());
        return dto;
    }

    public Document toEntity(DocumentDto dto) {
        Document document = new Document();
        document.setName(dto.getName());
        document.setFilePath(dto.getFilePath());
        return document;
    }

    public List<DocumentDto> toDtoList(List<Document> documentList) {
        List<DocumentDto> documentDtoList = new ArrayList<>();

        for (Document document : documentList) {
            documentDtoList.add(toDTO(document));
        }

        return documentDtoList;
    }
}
