package com.inhouse.archive.handle_files.service;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileProcessorFactory {

    @Value("${input.file.base.path.input}")
    private String basePathInput;

    @Value("${input.file.base.path.output}")
    private String basePathOutput;

    private final RedissonClient redissonClient;
    
    @Autowired
    public FileProcessorFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    // factory for reading files at once
    public ReadFileProcessor getReadFileProcessor(String fileName) {
        return new ReadFileProcessor(basePathInput + "\\" + fileName);
    }

    // factory for reading files in chunks
    public ReadFileChunkProcessor getReadFileChunkProcessor(String filePath, long startByte, long endByte) {
        return new ReadFileChunkProcessor(filePath, startByte, endByte);
    }

    // factory for writing files
    public WriteFileProcessor getWriteFileProcessor(String fileName) {
        return new WriteFileProcessor(basePathOutput + "\\" + fileName, redissonClient);
    }
}
