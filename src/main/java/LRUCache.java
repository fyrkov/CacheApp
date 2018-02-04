import java.util.*;

public class LRUCache implements Cache {

    private final LinkedHashMap<Integer, Entity> L1 = new LinkedHashMap<>();
    private final TreeMap<Long, Integer> L1_TIMESTAMPS = new TreeMap<>();
    private int cacheSize = 3;

    public Entity getObject(int id) {
        System.out.println("Looking up for object id = " + id + " in cache");
        if (!L1.containsKey(id)) {
            System.out.println("Record not found");
            return null;
        }
        updateStamp(id);
        return L1.get(id);
    }

    private void updateStamp(int id) {
        printCache();
        System.out.println("Updating timestamp in cache for object id = " + id);
        L1_TIMESTAMPS.values().remove(id);
        printCache();
        L1_TIMESTAMPS.put(System.nanoTime(), id);
        printCache();
    }

    @Override
    public void store(Entity object) {
        int id = object.getId();
        printCache();
        System.out.println("Storing in cache object id = " + id);
//        System.out.println("Cache size = " + L1.size());
        if (L1.size() == cacheSize) {
            // removing oldest record
            int id_to_remove = L1_TIMESTAMPS.pollFirstEntry().getValue();
            System.out.println("Cache is full. Removing record in cache for object id = " + id_to_remove);
            L1.remove(id_to_remove);
        }
        L1.put(id, object);
        L1_TIMESTAMPS.put(System.nanoTime(), id);
        printCache();
    }

    public void printCache() {
        System.out.println("CACHE = {");
//        L1_TIMESTAMPS.values().forEach(v -> System.out.print(" {" + v + ", " + L1.get(v) + "}"));
        L1_TIMESTAMPS.keySet().stream().sorted(Comparator.reverseOrder())
                .forEach(k -> System.out.println(" {" + L1_TIMESTAMPS.get(k) + ", " + L1.get(L1_TIMESTAMPS.get(k)) + "}"));
        System.out.println("}");
    }
}
