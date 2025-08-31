package com.inhouse.archive.handle_files.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MultiThreadConfig {

    @Value("${max.threads}")
	private int MAX_THREADS;
    @Value("${max.tasks.queued}")
	private int MAX_TASKS_QUEUED;

    public MultiThreadConfig() {
 
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
