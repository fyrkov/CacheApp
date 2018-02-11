public class DataAccessLayer {

    private final Cache cache = new LRUCache(1, 5);
    private DataSource dataSource = new CustomDataSource();
    private int cacheMissCount = 0;

    public Entity getObject(int id) {

        Entity object;
        // primitive synchronization
        synchronized (cache) {
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
}
