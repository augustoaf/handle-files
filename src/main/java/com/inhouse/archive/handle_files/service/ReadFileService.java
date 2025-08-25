package com.inhouse.archive.handle_files.service;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReadFileService {

    @Value("${input.file.base.path.input}")
    private String basePathInput;

    @Value("${input.file.base.path.output}")
    private String basePathOutput;

    private final RedissonClient redissonClient;
    
    @Autowired
    public ReadFileService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    // factory for reading files
    public ReadFileProcessor getFileProcessor(String fileName) {
        return new ReadFileProcessor(basePathInput + "\\" + fileName);
    }

    // factory for writing files
    public WriteFileProcessor getWriteFileProcessor(String fileName) {
        return new WriteFileProcessor(basePathOutput + "\\" + fileName, redissonClient);
    }
}
