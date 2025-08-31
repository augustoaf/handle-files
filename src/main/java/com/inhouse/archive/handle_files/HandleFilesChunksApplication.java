package com.inhouse.archive.handle_files;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.inhouse.archive.handle_files.service.FileProcessorFactory;
import com.inhouse.archive.handle_files.service.ReadAndWriteService;
import com.inhouse.archive.handle_files.service.ReadFileAbstract;
import com.inhouse.archive.handle_files.service.WriteFileProcessor;
import com.inhouse.dto.FileChunkDTO;

/*	
 * This is a Spring Boot application that demonstrates concurrent file reading and writing
 * considering inputs from Redis queue. Each queue item is a file chunk (start to end in bytes).
 */

@SpringBootApplication
public class HandleFilesChunksApplication {

    private static String CHUNKS_QUEUE;
	private static int MAX_THREADS;
	private static int MAX_TASKS_QUEUED;
		
    public static void main(String[] args) throws InterruptedException {
        
        // When this main method is executed, it runs before Spring initialize
		// the application context and perform dependency injection, then you can 
		// run the Spring Boot application manually to get access to the Beans 
        ApplicationContext context = SpringApplication.run(HandleFilesChunksApplication.class, args);

        // Read application.properties 
        CHUNKS_QUEUE = context.getEnvironment().getProperty("file.chunks.queue.name");
		MAX_THREADS = Integer.valueOf(context.getEnvironment().getProperty("max.threads"));
		MAX_TASKS_QUEUED = Integer.valueOf(context.getEnvironment().getProperty("max.tasks.queued"));

        // Get the FileProcessorFactory bean and instantiate shared output and error file processors
        FileProcessorFactory fileProcessorFactory = context.getBean(FileProcessorFactory.class);		
		WriteFileProcessor writeOutput = fileProcessorFactory.getWriteFileProcessor("output.txt");
		WriteFileProcessor writeError = fileProcessorFactory.getWriteFileProcessor("error.txt");	
		
        // Get the RedissonClient bean to interact with Redis
        RedissonClient redissonClient = context.getBean(RedissonClient.class);

		RBlockingQueue<FileChunkDTO> queue = redissonClient.getBlockingQueue(CHUNKS_QUEUE, new JsonJacksonCodec());
		queue.clear();//for testing purpose to clear the queue on each run
        
		System.out.println("STARTING CONSUMER LOOP...");
		
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;

		// loop to consume from a Redis List - each item represents a file chunk
		while (true) {

			// Check the size of the tasks in the thread queue in order to avoid overload the pool with many tasks 
			// waiting to be executed. If the queue is full, pause the Redis pull for a while
            int tasksInQueue = threadPoolExecutor.getQueue().size();
            if (tasksInQueue >= MAX_TASKS_QUEUED) {
                System.out.println("Too many tasks in the queue (" + tasksInQueue + " tasks). Pausing Redis pull.");
                Thread.sleep(5000); // Wait  5 second before checking again
                continue; // next loop iteration
            }

			FileChunkDTO chunk = queue.take(); // This will block until an item is available (queue is RBlockingQueue)
			System.out.println("Dequeued chunk for processing: " + chunk.getFileName() + " from " + chunk.getStartByte() + " to " + chunk.getEndByte());
			
			ReadFileAbstract readInput = fileProcessorFactory.getReadFileChunkProcessor(
				chunk.getFilePath(), chunk.getStartByte(), chunk.getEndByte());

			ReadAndWriteService readAndWriteTask = new ReadAndWriteService(readInput, writeOutput, writeError);
			
			threadPoolExecutor.submit(readAndWriteTask);
		}
    }
}
