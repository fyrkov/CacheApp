public class DataAccessLayer {
    private Cache cache = new LRUCache();
    private DataSource dataSource = new CustomDataSource();

    public Entity getObject(int id) {
        Entity object = cache.getObject(id);
        if (object == null) {
            object = dataSource.getObject(id);
            cache.store(object);
        }
        return object;
    }
}