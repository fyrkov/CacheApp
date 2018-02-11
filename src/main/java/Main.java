import java.io.FileWriter;
import java.io.IOException;
public class Main {

    public static void main(String[] args) {
        DataAccessLayer dal = new DataAccessLayer();
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
//            System.out.println(object + "\n");
        }
        System.out.println("===========================================================");
        System.out.println("Time elapsed, sec: " + (System.currentTimeMillis() - startTime) / 1000);
        System.out.println("Cache hit count: " + (n - dal.getCacheMissCount()) + " of " + n + ", " + (n - dal.getCacheMissCount()) * 1.0 / n * 100 + "%");
    }
}