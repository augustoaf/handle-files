package com.inhouse.archive.handle_files.helper;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.inhouse.archive.handle_files.service.ReadAndWriteService;

/*
 * This is a pool for the ReadAndWriteService instances in order to be reused in the thread pool executor.
 * It is not expected to create new instances outside the pool, the amount of items available should
 * match the availability of the tasks in the thread pool as per the implementation/configuration set. 
 * Meaning: the amount of tasks 'queued' and 'in execution' should not be higher than this pool size.
 */

 @Component
public class ReadAndWriteServicePool {

    private ConcurrentLinkedQueue<ReadAndWriteService> collection;
    private int maxPoolSize;

    private ReadAndWriteServicePool(Config config) {
    
        maxPoolSize = config.getMAX_REUSABLE_OBJECTS();
        collection = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < maxPoolSize; i++) {
            collection.add(new ReadAndWriteService(this));
        }
    }

    public void returnToPool(ReadAndWriteService service) {
       
        synchronized(ReadAndWriteServicePool.class) { 
       
            if (collection.size() >= maxPoolSize) {
                System.out.println("!!!!!!! WARNING: Pool is full, cannot return the service instance.");//This is not expected, this instance will eventually be deleted by the java garbage collector.
                return;
            }
            collection.add(service);
        }
    }

    public ReadAndWriteService getFromPool() {

        synchronized(ReadAndWriteServicePool.class) { 
            if (collection.isEmpty()) {
                System.out.println("!!!!!!! WARNING: Created a new ReadAndWriteService instance outside the pool.");	
                return new ReadAndWriteService(this);
            }
            
            //retrieve and remove from the pool 
            return collection.poll();
        }
    }   
}
