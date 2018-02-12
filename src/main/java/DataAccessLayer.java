/**
 * This class encapsulates access to data.
 * At first it looks for data in cache,  and if doesn't find,
 * then fetches data from datasource (f.i. DB) and stores it in cache (lazy cache).
 *
 * Assumptions:
 * <li> No cache invalidation policy is implemented.
 * <li> Cache is synchronized in most simple way.
 */
public class DataAccessLayer {

    private Cache cache;
    private DataSource dataSource;
    private int cacheMissCount;
    private final Object lock = new Object();

    private DataAccessLayer(Class cacheClass, int cache_L1_size, int cache_L2_size) {
        this.cache = CacheProvider.getCache(cacheClass, cache_L1_size, cache_L2_size);
        this.dataSource = new CustomDataSource();
        this.cacheMissCount = 0;
    }

    public Entity getObject(int id) {

        Entity object;
        synchronized (lock) {
            object = cache.getObject(id);
        }
        if (object == null) {
            cacheMissCount++;
            object = dataSource.getObject(id);
            cache.store(object);
        }
        return object;
    }

    public int getCacheMissCount() {
        return cacheMissCount;
    }

    public static class InstanceHolder {
        private static DataAccessLayer instance;
        public static synchronized DataAccessLayer getInstance(Class cacheClass, int cache_L1_size, int cache_L2_size) {
            return instance == null? createInstance(cacheClass, cache_L1_size, cache_L2_size) : instance;
        }

        private static DataAccessLayer createInstance(Class cacheClass, int cache_L1_size, int cache_L2_size) {
            instance = new DataAccessLayer(cacheClass, cache_L1_size, cache_L2_size);
            return instance;
        }
    }
}
