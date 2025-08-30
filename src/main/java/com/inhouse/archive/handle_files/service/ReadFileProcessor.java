package com.inhouse.archive.handle_files.service;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Concrete implementation of ReadFileAbstract to read one entire file from disk
 */
public class ReadFileProcessor extends ReadFileAbstract {

    public ReadFileProcessor(String filePath) {
        super(filePath);
    }

    @Override
    protected void initialize() {
        try {
			super.lines = Files.readAllLines(Paths.get(super.filePath));

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
