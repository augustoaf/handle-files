package com.inhouse.archive.handle_files.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReadFileService {

    @Value("${input.file.base.path}")
    private String basePath;

    // factory
    public ReadFileProcessor getFileProcessor(String fileName) {
        return new ReadFileProcessor(basePath + "\\" + fileName);
    }
}
