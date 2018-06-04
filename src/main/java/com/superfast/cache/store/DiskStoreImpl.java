package com.superfast.cache.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.superfast.cache.exception.CacheException;

/**
 * A datastore implementation which stores the cache entries on disk. There is a
 * configurable root folder which is set at startup time. All the methods are
 * write behind with respect to the in-memory read/write cache. This
 * implementation uses a single thread executor to doing all file IO so that the
 * order of cache updates/deletes are maintained as tasks are guaranteed to execute serially. A more sophisticated disk
 * implementation could have used batching and using only the last update of a
 * particular key is multiple updates are received for same key.
 *
 * @author Shailendra.Kumar2
 */

public class DiskStoreImpl implements DiskStore {

	private static final String defaultRootFolderPath = "D:/tmp";
	private static final int DEFAULT_TRANSFER_SIZE = 1024 * 1024;

	private String root = defaultRootFolderPath;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public DiskStoreImpl() {

	}

	public DiskStoreImpl(String rootFolder) {

		this.root = rootFolder;

	}

	public void initialize() throws IOException {
		Path rootDir = Paths.get(root);
		Path dir;
		if (Files.exists(rootDir)) {
			if (!Files.isDirectory(rootDir)) {
				throw new IOException("Not a directory: " + rootDir);
			}
			dir = rootDir.toRealPath();
		} else {
			dir = Files.createDirectories(rootDir);
		}

		root = dir.toString();


	}

	@Override
	public ByteBuffer get(String key) throws IOException {
		Path path = Paths.get(root, key);
		if (Files.notExists(path)) {
			return null;
		}
		try {
			try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
				try (FileChannel fileChannel = raf.getChannel()) {
					ByteBuffer buffer = fileChannel.map(MapMode.READ_ONLY, 0, fileChannel.size());
					return buffer;
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}

	@Override
	public void removeAsync(String key) throws CacheException {
		DiskAsyncWriter.write(new Runnable() {
			public void run() {
				try {
					Files.deleteIfExists(Paths.get(root, key));

				} catch (IOException e) {
					throw new CacheException(e);
				}
			}

		});

	}

	@Override
	public void putAsync(String key, byte[] value) throws CacheException {
		DiskAsyncWriter.write(new Runnable() {

			public void run() {
				Path path = Paths.get(root, key);
				Path parent = path.getParent();
				if (parent == null) {
					throw new CacheException("Unable to find parent of path: " + path);
				}
				if (Files.notExists(parent)) {
					try {
						Files.createDirectories(parent);
					} catch (Throwable e) {

						throw new CacheException(e);
					}
				}
				try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw")) {
					try (FileChannel fileChannel = raf.getChannel()) {
						int size = value.length;
						fileChannel.truncate(size);
						int length, offset = 0;
						while (offset < size) {
							length = Math.min((size - offset), DEFAULT_TRANSFER_SIZE);
							ByteBuffer buffer = ByteBuffer.wrap(value, offset, length);
							offset += fileChannel.write(buffer);
						}

						fileChannel.force(true);
					}
				} catch (Throwable e) {
					try {
						Files.deleteIfExists(path);
					} catch (IOException ioe) {

					}
					throw new CacheException(e);
				}

			}
		});
	}

	@Override
	public String getPersistenceRootDirectory() {
		return getRoot();
	}

	@Override
	public void clearAll() {
		Arrays.stream(new File(getPersistenceRootDirectory()).listFiles()).forEach(File::delete);
	}

}
