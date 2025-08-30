package com.inhouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.nio.file.Paths;

// TODO: why JsonProprties are not being considered when reading from Redis?
// TODO: also, how to parse the read from Redis Queue without needing to have this class in the same path as the producer?

public class FileChunkDTO implements Serializable {

    @JsonProperty("file_path")
    private String filePath;
    @JsonProperty("start_byte")
    private long startByte;
    @JsonProperty("end_byte")
    private long endByte;

    // A no-argument constructor is needed for deserialization frameworks like Jackson
    public FileChunkDTO() {
    }

    public FileChunkDTO(String filePath, long startByte, long endByte) {
        this.filePath = filePath;
        this.startByte = startByte;
        this.endByte = endByte;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getStartByte() {
        return startByte;
    }

    public void setStartByte(long startByte) {
        this.startByte = startByte;
    }

    public long getEndByte() {
        return endByte;
    }

    public void setEndByte(long endByte) {
        this.endByte = endByte;
    }

    public String getFileName() {
        return Paths.get(this.filePath).getFileName().toString();
    }   

    @Override
    public String toString() {
        return "FileChunkDTO{" + "filePath='" + filePath + '\'' + ", startByte=" + startByte + ", endByte=" + endByte + '}';
    }
}
