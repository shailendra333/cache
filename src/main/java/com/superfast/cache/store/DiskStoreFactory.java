package com.superfast.cache.store;

public class DiskStoreFactory {

	public static DiskStore getDiskStore(String root) {
		DiskStore diskStore = new DiskStoreImpl(root);
		return diskStore;

	}

}
