public interface Cache {

    Entity getObject(int id);

    void store(Entity object);
}
