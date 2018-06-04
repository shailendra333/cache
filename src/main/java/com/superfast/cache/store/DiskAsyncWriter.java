package com.superfast.cache.store;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DiskAsyncWriter {

	private static ExecutorService executorService = Executors.newSingleThreadExecutor( new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });


	public static void write(Runnable task) {
		executorService.submit(task);
	}

//	public static void shutdown(){
//		executorService.shutdown();
//	}

	public static void awaitTermination() throws InterruptedException{
		executorService.awaitTermination(5, TimeUnit.SECONDS);
	}

}
