package com.inhouse.archive.handle_files.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class WriteFileProcessor {

    private String filePath;

    public WriteFileProcessor(String filePath) {
        this.filePath = filePath;
    }

    public void writeLine(String line) throws IOException {
        Files.write(
            Paths.get(filePath),
            java.util.Collections.singletonList(line + " : " + LocalDateTime.now() + " : " + Thread.currentThread().getName()),
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }
    
    public void clearFile() throws IOException {
        Files.write(Paths.get(filePath), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
 
}