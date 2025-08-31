package com.inhouse.archive.handle_files.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Value("${file.chunks.queue.name}")
    private String QUEUE_NAME;
    @Value("${max.threads}")
	private int MAX_THREADS;
    @Value("${max.tasks.queued}")
	private int MAX_TASKS_QUEUED;

    public Config() {
 
    }
        
    public String getQUEUE_NAME() {
		return QUEUE_NAME;
	}

	public int getMAX_THREADS() {
		return MAX_THREADS;
	}

	public int getMAX_TASKS_QUEUED() {
		return MAX_TASKS_QUEUED;
	} 

    public int getMAX_REUSABLE_OBJECTS() {
        return MAX_TASKS_QUEUED + MAX_THREADS;
    }
}
