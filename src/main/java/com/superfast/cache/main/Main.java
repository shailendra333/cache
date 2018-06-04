package com.superfast.cache.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.superfast.cache.KVStore;
import com.superfast.cache.exception.CacheException;
import com.superfast.cache.impl.KVStoreFactory;
import com.superfast.cache.store.DiskAsyncWriter;
import com.superfast.cache.store.DiskStore;
import com.superfast.cache.store.DiskStoreFactory;


/**
 * Main method for running a demo of cache operations
 * @author Shailendra.Kumar2
 *
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		String mainConfigFilePath = null;
		if (args.length == 1)
			mainConfigFilePath = args[0];
		else
			mainConfigFilePath = "D:/cache-store-config.properties";

		// externalize the properties
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(mainConfigFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String rootFolder = props.getProperty("rootFolder");
		DiskStore diskStore = DiskStoreFactory.getDiskStore(rootFolder);
		String initialCapacity = props.getProperty("initialCapacity");
		Integer initCapacity = new Integer(initialCapacity);
		String loadFactor = props.getProperty("loadFactor");
		Float lf = Float.valueOf(loadFactor);

		KVStore kvStore = KVStoreFactory.getKVStore(initCapacity,lf,diskStore);


		for( int i=0 ; i< 200;i++){

			try {
				kvStore.put("Shailendra"+i, "My String "+i);
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// wait for sometime to let all the IO happen before the main program exits.
		//In a typical non terminating cache server, this will not be needed as the cache
		//process is supposed to be running infinitely listening for requests for CRUD operation
		DiskAsyncWriter.awaitTermination();

	}

}
