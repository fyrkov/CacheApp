public class Main {

    public static void main(String[] args) {
        DataAccessLayer dal = new DataAccessLayer();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            int id = Double.valueOf(Math.random() * 10).intValue();
            Entity object = dal.getObject(id);
            System.out.println(object + "\n");
        }
        System.out.println("Time elapsed, sec: " + (System.currentTimeMillis() - startTime) / 1000);
    }
}