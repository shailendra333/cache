package com.superfast.cache.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.superfast.cache.KVStore;
import com.superfast.cache.exception.CacheException;
import com.superfast.cache.store.DiskStore;


/**
 * This is a simple implementation of read/write cache which stores the data on disk in a configurable directory.
 * The data write to disk happens in a seperate single thread asynchronously which performs IO operations in order it recieves requests.
 * The underlying datastructure is Java ConcurrentHashmap which allows for safe concurrent updates. Initial capacity, load factor and the cache storage
 * directory is configurable via a properties file which can be supplied at run-time.
 *
 * For large number of objects and using the automatic file persistence ( using mapped memory) - another solution which is widely used - offheap memory allocation
 * could have been used. Using off heap memory would reduce the GC pressure on large heaps as caches typically tend to have large heaps with certain amount of
 * hot data access pattern. The mmapped files are managed by OS itself and the dirty files are flushed automatically by OS to the underlying file store.
 *
 * @author Shailendra.Kumar2
 *
 * @param <K>
 * @param <V>
 */
public class KVStoreImpl<K, V> implements KVStore<K, V> {

	private static Logger logger = LoggerFactory.getLogger( KVStoreImpl.class );

	ConcurrentHashMap<K, V> cache;
	DiskStore cacheDataStore;

	public KVStoreImpl(int initialCapacity, float loadFactor, DiskStore dStore) {
		cache = new ConcurrentHashMap<K, V>(initialCapacity, loadFactor);
		cacheDataStore = dStore;

	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
		if (cacheDataStore != null) {
			try {
				cacheDataStore.putAsync(key.toString(), convertObjectToByte(value));
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				logger.error("Exception occured", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception occured", e);
			}
		}

	}

	@Override
	public void delete(K key) {
		cache.remove(key);
		if (cacheDataStore != null) {
			cacheDataStore.removeAsync(key.toString());
		}

	}

	@Override
	public void clear() {
		cache.clear();
		if (cacheDataStore != null) {
			cacheDataStore.clearAll();
		}

	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return cache.size();
	}

	public DiskStore getCacheDataStore() {
		return cacheDataStore;
	}

	public void setCacheDataStore(DiskStore cacheDataStore) {
		this.cacheDataStore = cacheDataStore;
	}

	// protected void persist(K key, V value, OutputStream outputStream) throws
	// IOException {
	// ObjectOutputStream objectOutputStream = new
	// ObjectOutputStream(outputStream);
	// objectOutputStream.writeObject(value);
	// objectOutputStream.flush();
	// }
	//
	// public V readPersisted(K key, InputStream inputStream) throws IOException
	// {
	// try {
	// return (V) new ObjectInputStream(inputStream).readObject();
	// } catch (ClassNotFoundException e) {
	// throw new RuntimeException(String.format("Serialized version invalid",
	// key), e);
	// }
	// }

	protected List<String> directoryFor(K key) {
		return Arrays.asList(key.toString());
	}

	public File getPersistenceRootDirectory() {
		return new File(cacheDataStore.getPersistenceRootDirectory());
	}

	public byte[] convertObjectToByte(Object obj) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		byte[] data = bos.toByteArray();
		return data;
	}

}
