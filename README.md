# cache
This is a simple implementation of read/write cache which stores the data on disk in a configurable directory.
The data write to disk happens in a seperate single thread asynchronously which performs IO operations in order it recieves requests.
The underlying datastructure is Java ConcurrentHashmap which allows for safe concurrent updates. Initial capacity, load factor and the cache storage  directory is configurable via a properties file which can be supplied at run-time. 
For large number of objects and using the automatic file persistence ( using mapped memory) - another solution which is widely used - offheap memory allocation could have been used. Using off heap memory would reduce the GC pressure on large heaps as caches typically tend to have large heaps with certain amount of hot data access pattern. The mmapped files are managed by OS itself and the dirty files are flushed automatically by OS to the underlying file store.
 
Also this contains a datastore implementation which stores the cache entries on disk. There is a configurable root folder which is set at startup time. All the methods are   write behind with respect to the in-memory read/write cache. This  implementation uses a single thread executor to doing all file IO so that the order of cache updates/deletes are maintained as tasks are guaranteed to execute serially. A more sophisticated disk implementation could have used batching and using only the last update of a
particular key is multiple updates are received for same key.
