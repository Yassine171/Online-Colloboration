package com.online.colloboration.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File(directory.getAbsolutePath() + File.separator + fileName);
        multipartFile.transferTo(file);
    }
}
