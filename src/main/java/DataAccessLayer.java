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

    private DataAccessLayer() {
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

    public void setCache(Class cacheClass, int memCacheSize, int diskCacheSize) {
        this.cache = CacheProvider.getCache(cacheClass, memCacheSize, diskCacheSize);
        this.dataSource = new CustomDataSource();
        this.cacheMissCount = 0;
    }

    private static class InstanceHolder {
        private static DataAccessLayer instance = new DataAccessLayer();
    }
    public static DataAccessLayer getInstance() {
        return InstanceHolder.instance;
    }
}
