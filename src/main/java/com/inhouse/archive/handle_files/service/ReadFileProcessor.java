package com.inhouse.archive.handle_files.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.inhouse.archive.handle_files.helper.StringWrapper;

public class ReadFileProcessor {

    private String filePath;
    private List<String> lines;
    private int lineIndex = 0;

    public ReadFileProcessor(String filePath) {
        this.filePath = filePath;
        start();
    }

    private void start() {
        try {
			lines = Files.readAllLines(Paths.get(filePath));

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public boolean readNext(StringWrapper lineValue) {
        
        if (lines.size() > 0 && lineIndex < lines.size()) {

            lineValue.setValue(lines.get(lineIndex++));
            return true;
        } else {
            return false;
        }
    }
}
