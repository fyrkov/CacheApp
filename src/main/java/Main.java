import java.io.FileWriter;
import java.io.IOException;

/**
 * Task:
 * "Create a configurable two-level cache (for caching Objects).
 * Level 1 is memory, level 2 is filesystem.
 * Config params should let one specify the cache strategies and max sizes of level  1 and 2."
 */
public class Main {

    public static void main(String[] args) {
        //Here cache strategies and max sizes of level 1 and 2 can be specified
        DataAccessLayer dal = DataAccessLayer.InstanceHolder.getInstance(LRUCache.class, 2, 5);
        try (FileWriter fw = new FileWriter(LRUCache.cacheFile)) {
            fw.write("");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int n = 10;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            System.out.println("===========================================================");
            int id = Double.valueOf(Math.random() * 10).intValue();
            Entity object = dal.getObject(id);
        }
        System.out.println("===========================================================");
        System.out.println("Time elapsed, sec: " + (System.currentTimeMillis() - startTime) / 1000);
        System.out.println("Cache hit count: " + (n - dal.getCacheMissCount())
                + " of " + n + ", " + (n - dal.getCacheMissCount()) * 1.0 / n * 100 + "%");
    }
}