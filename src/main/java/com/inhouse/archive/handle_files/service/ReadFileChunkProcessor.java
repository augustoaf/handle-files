package com.inhouse.archive.handle_files.service;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Concrete implementation of ReadFileAbstract to read a chunk of a file from disk
 * (chunk = start to end in bytes of a file)
 */

public class ReadFileChunkProcessor extends ReadFileAbstract {

    private long startByte;
    private long endByte;   

    public ReadFileChunkProcessor(String filePath, long startByte, long endByte) {
        super(filePath);
        this.startByte = startByte;
        this.endByte = endByte;
    }

    @Override
    protected void initialize() {

        super.lines = new ArrayList<>();

        // TODO: some lines are split between chunks, need to handle that
        try (FileChannel fileChannel = FileChannel.open(Paths.get(super.getFilePath()), StandardOpenOption.READ)) {
            long chunkSize = endByte - startByte;
            if (chunkSize <= 0) {
                return;
            }

            // Map the specified portion of the file into memory
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startByte, chunkSize);

            if (!buffer.hasRemaining()) {
                return;
            }

            // Decode the byte buffer into a char buffer and then to a string
            String content = StandardCharsets.UTF_8.decode(buffer).toString();

            // Split the content into lines and add to the list
            // The \\R matches any Unicode linebreak sequence.
            super.lines.addAll(Arrays.asList(content.split("\\R")));

        } catch (IOException e) {
            // In a real application, use a logger instead of printing the stack trace
            e.printStackTrace();
            super.lines = Collections.emptyList();
        }
    }

}
