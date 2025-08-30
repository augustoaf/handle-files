package com.inhouse.archive.handle_files.service;

import java.nio.file.Paths;
import java.util.List;

import com.inhouse.archive.handle_files.helper.StringWrapper;

public abstract class ReadFileAbstract {

    protected String filePath;
    protected List<String> lines;
    private int lineIndex = 0;

    // A lock for each instances of this class 
    // (guarantee a distinct line read per thread when multiple treads execute readNext 
    //  from the same ReadFileProcessor instance) 
    private final Object FILE_LOCK = new Object();

    public ReadFileAbstract(String filePath) {
        this.filePath = filePath;
    }

    // concrete classes must implement to load the file lines into the lines List
    protected abstract void initialize();

    public boolean readNext(StringWrapper lineValue) {
        
        synchronized(FILE_LOCK) {

            if (lines == null) {
                initialize();
            }

            if (lines.size() > 0 && lineIndex < lines.size()) {

                lineValue.setValue(lines.get(lineIndex++));
                return true;
            } else {
                return false;
            }
        }    
    }

    public String getFileName() {
        return Paths.get(this.filePath).getFileName().toString();
    }

    public String getFilePath() {
        return this.filePath;
    }
}
