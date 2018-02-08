import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class LRUCache implements Cache {

    private final LinkedHashMap<Integer, Entity> L1 = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Entity> L2 = new LinkedHashMap<>();
    private final TreeMap<Long, Integer> L1_LOOKUP_TABLE = new TreeMap<>();
    private final TreeMap<Long, Integer> L2_LOOKUP_TABLE = new TreeMap<>();
    private int cacheSize;
    private Scanner scanner;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        scanner = null;
        try {
            scanner = new Scanner(new File("1.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Entity getObject(int id) {
        System.out.println("Looking up for object id = " + id + " in cache");
        if (L1_LOOKUP_TABLE.containsValue(id)) {
            System.out.println("Record is found in L1");
            updateStamp1(id);
            return L1.get(id);
        } else if (L2_LOOKUP_TABLE.containsValue(id)) {
            System.out.println("Record is found in L2");
            updateStamp2(id);
            return getFromFile(id);
        }
        return null;
    }

    private Entity getFromFile(int id) {
        Entity obj = null;
        try (FileReader fr = new FileReader("1.txt")) {
            scanner = new Scanner(fr);
            if (scanner.hasNext()) {
                String s = scanner.nextLine();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(s);
                if (id == (Integer) json.get("Id")) {
                    obj = new Entity((Integer) json.get("Id"));
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void updateStamp1(int id) {
        printCache();
        System.out.println("Updating timestamp in cache for object id = " + id);
        L1_LOOKUP_TABLE.values().remove(id);
        printCache();
        L1_LOOKUP_TABLE.put(System.nanoTime(), id);
        printCache();
    }

    private void updateStamp2(int id) {
        printCache();
        System.out.println("Updating timestamp in cache for object id = " + id);
        L2_LOOKUP_TABLE.values().remove(id);
        printCache();
        L1_LOOKUP_TABLE.put(System.nanoTime(), id);
        printCache();
    }

    @Override
    public void store(Entity object) {
        int id = object.getId();
        printCache();
        System.out.println("Storing in cache object id = " + id);
        if (L1.size() == cacheSize) {
            // removing oldest record
            int id_to_remove = L1_LOOKUP_TABLE.pollFirstEntry().getValue();
            System.out.println("Cache is full. Removing record in cache for object id = " + id_to_remove);
            L1.remove(id_to_remove);
        }
        L1.put(id, object);
        L1_LOOKUP_TABLE.put(System.nanoTime(), id);
        printCache();
        //todo handle L2 here
        storeToFile(object);
    }

    private void storeToFile(Entity object) {
        JSONObject obj = getJsonObject(object);
        try (FileWriter fw = new FileWriter("1.txt", true)) {
            fw.write(obj.toJSONString() + "\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonObject(Entity object) {
        JSONObject obj = new JSONObject();
        obj.put("Id", object.getId());
        obj.put("SomeProperty", object.getSomeProperty());
        obj.put("Name", object.getName());
        return obj;
    }

    public void printCache() {
        System.out.println("CACHE = {");
        L1_LOOKUP_TABLE.keySet().stream().sorted(Comparator.reverseOrder())
                .forEach(k -> System.out.println(getJsonObject(L1.get(L1_LOOKUP_TABLE.get(k)))));
        System.out.println("}");
    }
}
