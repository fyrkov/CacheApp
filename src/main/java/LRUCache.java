import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class LRUCache implements Cache {

    private final LinkedHashMap<Integer, Entity> L1 = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Entity> L2 = new LinkedHashMap<>();
    private final TreeMap<Long, Integer> L1_TIMESTAMPS = new TreeMap<>();
    private int cacheSize;
    private final List<JSONObject> jsonArray;
    private Scanner scanner;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        scanner = null;
        jsonArray = new ArrayList<>();
        try {
            scanner = new Scanner(new File("1.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        if (L1.size() == cacheSize) {
            // removing oldest record
            int id_to_remove = L1_TIMESTAMPS.pollFirstEntry().getValue();
            System.out.println("Cache is full. Removing record in cache for object id = " + id_to_remove);
            L1.remove(id_to_remove);
        }
        L1.put(id, object);
        L1_TIMESTAMPS.put(System.nanoTime(), id);
        printCache();
        //todo handle L2 here
        JSONObject obj = new JSONObject();
        obj.put("Id", object.getId());
        obj.put("Name", object.getName());
        obj.put("SomeProperty", object.getSomeProperty());
        try (FileWriter file = new FileWriter("1.txt",true)) {
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            while (scanner.hasNext()) {
//                JSONObject obj = (JSONObject) new JSONParser().parse(scanner.nextLine());
//                jsonArray.add(obj);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }

    public void printCache() {
        System.out.println("CACHE = {");
//        L1_TIMESTAMPS.values().forEach(v -> System.out.print(" {" + v + ", " + L1.get(v) + "}"));
        L1_TIMESTAMPS.keySet().stream().sorted(Comparator.reverseOrder())
                .forEach(k -> System.out.println(" {" + L1_TIMESTAMPS.get(k) + ", " + L1.get(L1_TIMESTAMPS.get(k)) + "}"));
        System.out.println("}");
    }
}
