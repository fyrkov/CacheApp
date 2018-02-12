public class CacheProvider {

    public static Cache getCache(Class cacheClass, int L1_size, int L2_size) {
        Cache cache = null;
        try {
            cache = (Cache) cacheClass.getDeclaredConstructor(int.class, int.class).newInstance(L1_size, L2_size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }
}
