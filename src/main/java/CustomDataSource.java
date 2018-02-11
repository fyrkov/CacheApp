public class CustomDataSource implements DataSource {
    @Override
    public Entity getObject(int id) {
        final Entity[] obj = new Entity[1];
        Thread t = new Thread(() -> {
            System.out.println("Object is not found in cache. Fetching data from datasource");
            try {
                Thread.sleep(1000L);
                obj[0] = new Entity(id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return obj[0];
    }

}