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

import com.inhouse.archive.handle_files.helper.Config;
import com.inhouse.archive.handle_files.helper.ReadAndWriteServicePool;
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

    private static String QUEUE_NAME;
	private static int MAX_THREADS;
	private static int MAX_TASKS_QUEUED;
		
    public static void main(String[] args) throws InterruptedException {
        
        // When this main method is executed, it runs before Spring initialize
		// the application context and perform dependency injection, then you can 
		// run the Spring Boot application manually to get access to the Beans 
        ApplicationContext context = SpringApplication.run(HandleFilesChunksApplication.class, args);

		Config config = context.getBean(Config.class);
        QUEUE_NAME = config.getQUEUE_NAME();
		MAX_THREADS = config.getMAX_THREADS();
		MAX_TASKS_QUEUED = config.getMAX_TASKS_QUEUED();

        // Get the FileProcessorFactory bean and instantiate shared output and error file processors
        FileProcessorFactory fileProcessorFactory = context.getBean(FileProcessorFactory.class);		
		WriteFileProcessor writeOutput = fileProcessorFactory.getWriteFileProcessor("output.txt");
		WriteFileProcessor writeError = fileProcessorFactory.getWriteFileProcessor("error.txt");	
		
        // Get the RedissonClient bean to interact with Redis
        RedissonClient redissonClient = context.getBean(RedissonClient.class);
		RBlockingQueue<FileChunkDTO> queue = redissonClient.getBlockingQueue(QUEUE_NAME, new JsonJacksonCodec());
		queue.clear();//for testing purpose to clear the queue on each run
        
		// Create a fixed thread pool executor
		ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;

		// Create a pool to hold reusable ReadAndWriteService instances
		ReadAndWriteServicePool servicesPool = context.getBean(ReadAndWriteServicePool.class);

		System.out.println("##### STARTING CONSUMER LOOP ##### \n");
		
		// loop to consume from a Redis List (each item represents a file chunk)
		while (true) {

			System.out.println("!!! thread queue: " + threadPoolExecutor.getQueue().size());
			System.out.println("!!! thread active count: " + threadPoolExecutor.getActiveCount());
			
			// Check the size of the thread pool executor queue in order to avoid overload
			// the pool with many tasks waiting execution. If the queue is full, pause the Redis pull for a while.
            int tasksInQueue = threadPoolExecutor.getQueue().size();
            if (tasksInQueue >= MAX_TASKS_QUEUED) {
				int secondsToWait = 5;
                System.out.println("Too many tasks in the queue (" + tasksInQueue + " tasks). Pausing Redis pull for " + secondsToWait + " seconds...");
                Thread.sleep(secondsToWait * 1000); // Wait before checking again
                continue; // next loop iteration
            }

			FileChunkDTO chunk = queue.take(); // This will block until an item is available (queue is RBlockingQueue)
			System.out.println("Dequeued chunk for processing: " + chunk.getFileName() + " from " + chunk.getStartByte() + " to " + chunk.getEndByte());
			
			ReadFileAbstract readInput = fileProcessorFactory.getReadFileChunkProcessor(
				chunk.getFilePath(), chunk.getStartByte(), chunk.getEndByte());
			ReadAndWriteService readAndWriteTask = servicesPool.getFromPool(readInput, writeOutput, writeError);
			
			threadPoolExecutor.submit(readAndWriteTask);
		}
    }
}
