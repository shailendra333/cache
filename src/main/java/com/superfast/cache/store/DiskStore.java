package com.superfast.cache.store;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.superfast.cache.exception.CacheException;

/**
 * CRUD operations for disk persisted cache.
 *
 * @author Shailendra.kumar2
 */
public interface DiskStore {

	public void putAsync(String key, byte[] value) throws CacheException;

	public ByteBuffer get(String key) throws CacheException, IOException;

	public void removeAsync(String key) throws CacheException;

	public String getPersistenceRootDirectory();

	public void clearAll();
}
