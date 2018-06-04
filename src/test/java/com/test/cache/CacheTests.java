package com.test.cache;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.superfast.cache.KVStore;
import com.superfast.cache.exception.CacheException;
import com.superfast.cache.impl.KVStoreFactory;
import com.superfast.cache.store.DiskAsyncWriter;
import com.superfast.cache.store.DiskStore;
import com.superfast.cache.store.DiskStoreFactory;

public class CacheTests {

	String mainConfigFilePath = null;
	static KVStore kvStore = null;

	@BeforeClass
	public static void setup() {
		String mainConfigFilePath = "D:/cache-store-config.properties";
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
		kvStore = KVStoreFactory.getKVStore(initCapacity, lf, diskStore);

	}

	@AfterClass
	public static void tearDown() {
		try {
			DiskAsyncWriter.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testAssertMap() {
		for (int i = 0; i < 4; i++) {
			try {
				kvStore.put("Shailendra" + i, "My String " + i);
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		Map<String, String> expected = new HashMap<>();
		expected.put("shailendra1", "shailendra1");
		expected.put("shailendra2", "shailendra2");
		expected.put("shailendra3", "shailendra3");
		expected.put("shailendra4", "shailendra4");

		assertThat(kvStore, is(expected));

		assertThat(kvStore.size(), is(4));


	}

}
