package com.online.colloboration.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentDto {
        private Long id;
        private String name;
        private String filePath;
        private LocalDateTime uploadDate;
        private Long userId;

        // getters and setters

}
