package com.superfast.cache;

import java.io.IOException;

import com.superfast.cache.exception.CacheException;

public interface KVStore<K, V> {

	V get(K key);

	void put(K key, V value) ;

	void delete(K key);

	void clear();

	long size();
}