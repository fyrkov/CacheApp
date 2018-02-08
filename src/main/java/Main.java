import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        DataAccessLayer dal = new DataAccessLayer();
        try (FileWriter fw = new FileWriter("1.txt")) {
            fw.write("");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            int id = Double.valueOf(Math.random() * 10).intValue();
            Entity object = dal.getObject(id);
            System.out.println(object + "\n");
        }
        System.out.println("Time elapsed, sec: " + (System.currentTimeMillis() - startTime) / 1000);
    }
}