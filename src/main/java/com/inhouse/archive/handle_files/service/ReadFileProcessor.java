package com.inhouse.archive.handle_files.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.inhouse.archive.handle_files.helper.StringWrapper;

public class ReadFileProcessor {

    private String filePath;
    private List<String> lines;
    private int lineIndex = 0;

    // A lock for each instances of this class 
    // (guarantee a distinct line read per thread when multiple treads execute readNext 
    //  from the same ReadFileProcessor instance) 
    private final Object FILE_LOCK = new Object();

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
        
        synchronized(FILE_LOCK) {
            if (lines.size() > 0 && lineIndex < lines.size()) {

                lineValue.setValue(lines.get(lineIndex++));
                return true;
            } else {
                return false;
            }
        }    
    }
}
