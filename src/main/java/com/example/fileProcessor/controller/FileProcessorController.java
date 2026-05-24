package com.example.fileProcessor.controller;

import com.example.fileProcessor.service.CsvParser;
import com.example.fileProcessor.service.FileParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileProcessorController {

    @Autowired
    private FileParser csvParser;

    @PostMapping("/process")
    public ResponseEntity<String> processFile(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);
        csvParser.parse(tempFile);
        tempFile.deleteOnExit();

        return ResponseEntity.ok("File Processed Successfully!!!");
    }

}
