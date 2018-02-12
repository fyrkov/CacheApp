import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * 2 levels LRU ("least recently used") cache implementation.
 * Consists of L1 memory cache and L2 disk cache.
 * 2 corresponding lookup tables implemented with timestamp for each record.
 * L1 mem cache is implemented as LinkedHashMap.
 * L2 disk cache is implemented as file with JSON strings.
 *
 * Assumptions:
 * <li> Cache file "cache.json" is created on first launch in project folder.
 * <li> Cache level sizes are defined as number of objects, not as amount of data.
 * <li> Cache has significant overhead on rearranging data.
 * <li> Cache has inappropriate file format, could be better.
 * <li> Cache file downsizing is not optimal.
 * <li> Cache is not thread-safe. Synchronization is implemented in DataAccessLayer.
 */
public class LRUCache implements Cache {

    private final LinkedHashMap<Integer, Entity> L1 = new LinkedHashMap<>();
    private final TreeMap<Long, Integer> L1_LOOKUP_TABLE = new TreeMap<>();
    private final TreeMap<Long, Integer> L2_LOOKUP_TABLE = new TreeMap<>();
    private final int memCacheSize;
    private final int diskCacheSize;
    private JSONParser jsonParser;
    private JSONObject jsonObject;
    protected static final String cacheFile = "cache.json";
    private final String tmpFile = "cache.tmp";

    /**
     * @param memCacheSize - defines maximum number of objects that can be stored in L1
     * @param diskCacheSize - defines maximum number of objects that can be stored in L2
     */
    LRUCache(int memCacheSize, int diskCacheSize) {
        this.jsonParser = new JSONParser();
        this.memCacheSize = memCacheSize;
        this.diskCacheSize = diskCacheSize;
    }

    public Entity getObject(int id) {
        System.out.println("Looking up for object id = " + id + " in cache");
        if (L1_LOOKUP_TABLE.containsValue(id)) {
            System.out.println("Record is found in L1");
            Entity entity = L1.get(id);
            rearrangeCache(id, true);
            return entity;
        } else if (L2_LOOKUP_TABLE.containsValue(id)) {
            System.out.println("Record is found in L2");
            Entity entity = getFromFile(id);
            rearrangeCache(id, false);
            return entity;
        }
        return null;
    }

    /**
     * Stores object to cache.
     * Cache is represented by two queues.
     * If L1 reaches max size, oldest entry is put to L2.
     * If L2 reaches max size, oldest entry is deleted.
     * @param object
     */
    public void store(Entity object) {
        int id = object.getId();
        printCache();
        System.out.println("Storing in mem cache object id = " + id);
        if (L1.size() == memCacheSize) {
            int idToRemove = L1_LOOKUP_TABLE.pollFirstEntry().getValue();
            System.out.println("Mem Cache is full. Removing record for object id = " + idToRemove + " to disk cache");
            storeToL2(L1.get(idToRemove));
            L1.remove(idToRemove);
        }
        L1.put(id, object);
        L1_LOOKUP_TABLE.put(System.nanoTime(), id);
        printCache();
    }

    private void storeToL2(Entity object) {
        if (L2_LOOKUP_TABLE.size() == diskCacheSize) {
            int idToRemove = L2_LOOKUP_TABLE.pollFirstEntry().getValue();
            removeFromFile(idToRemove);
        }
        L2_LOOKUP_TABLE.put(System.nanoTime(), object.getId());
        jsonObject = getJsonObject(object);
        try (FileWriter fw = new FileWriter(cacheFile, true)) {
            fw.write(jsonObject.toJSONString() + "\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Re-order objects in cache after the object request.
     * If object is found in L1 then just replace it to the top of L1 lookup table queue.
     * If object is found in L2 then remove it from L2 cache and L2 lookup table queue and
     * store it again, so it will appear on the top of L1 cache.
     * @param id
     * @param inside_L1_only
     */
    private void rearrangeCache(int id, boolean inside_L1_only) {
        printCache();
        System.out.println("Updating record in cache for object id = " + id);
        if (inside_L1_only) {
            L1_LOOKUP_TABLE.values().remove(id);
            printCache();
            L1_LOOKUP_TABLE.put(System.nanoTime(), id);
        } else {
            Entity entity = getFromFile(id);
            L2_LOOKUP_TABLE.values().remove(id);
            removeFromFile(id);
            store(entity);
        }
        printCache();
    }

    private Entity getFromFile(int id) {
        Entity obj = null;
        String s;
        try (FileReader fr = new FileReader(cacheFile);
             Scanner sc = new Scanner(fr)) {
            while (sc.hasNext()) {
                s = sc.nextLine();
                jsonObject = (JSONObject) jsonParser.parse(s);
                if (id == Integer.parseInt(jsonObject.get("Id").toString())) {
                    obj = new Entity(id);
                    break;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void removeFromFile(int id) {
        String s;
        // cache -> tmp
        try (FileReader fr1 = new FileReader(cacheFile);
             FileWriter fw2 = new FileWriter(tmpFile);
             Scanner sc = new Scanner(fr1)
        ) {
            while (sc.hasNext()) {
                s = sc.nextLine();
                jsonObject = (JSONObject) jsonParser.parse(s);
                if (id != Integer.parseInt(jsonObject.get("Id").toString())) {
                    fw2.write(s + "\n");
                }
            }
            fw2.flush();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // tmp -> cache
        try (FileReader fr2 = new FileReader(tmpFile);
             FileWriter fw1 = new FileWriter(cacheFile);
             Scanner sc = new Scanner(fr2)
        ) {
            while (sc.hasNext()) {
                s = sc.nextLine();
                jsonObject = (JSONObject) jsonParser.parse(s);
                if (id != Integer.parseInt(jsonObject.get("Id").toString())) {
                    fw1.write(s + "\n");
                }
            }
            fw1.flush();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // cleaning tmp file
        try (FileWriter fw2 = new FileWriter(tmpFile)) {
            fw2.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonObject(Entity object) {
        jsonObject = new JSONObject();
        jsonObject.put("Id", object.getId());
        jsonObject.put("SomeProperty", object.getSomeProperty());
        jsonObject.put("Name", object.getName());
        return jsonObject;
    }

    private void printCache() {
        System.out.println("MEM CACHE = {");
        L1_LOOKUP_TABLE.keySet().stream().sorted(Comparator.reverseOrder())
                .forEach(k -> System.out.println("\t" + getJsonObject(L1.get(L1_LOOKUP_TABLE.get(k)))));
        System.out.println("}");
        System.out.println("DISK CACHE = {");
        L2_LOOKUP_TABLE.keySet().stream().sorted(Comparator.reverseOrder())
                .forEach(k -> System.out.println("\t" + getFromFile((L2_LOOKUP_TABLE.get(k)))));
        System.out.println("}");
    }
}
