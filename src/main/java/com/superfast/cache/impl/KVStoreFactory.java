package com.superfast.cache.impl;

import com.superfast.cache.KVStore;
import com.superfast.cache.store.DiskStore;

public class KVStoreFactory {

	public static KVStore getKVStore(int initCapacity, float loadfactor, DiskStore dStore) {
		KVStore kvStore = new KVStoreImpl(initCapacity, loadfactor,dStore);
		return kvStore;

	}
}
