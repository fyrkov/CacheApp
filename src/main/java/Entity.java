import java.io.Serializable;

public class Entity implements Serializable {
    private int id;
    private String name;
    private String someProperty;

    public Entity(int id) {
        this.id = id;
        this.name = "Name " + id;
        this.someProperty = String.valueOf(this.hashCode());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSomeProperty() {
        return someProperty;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", someProperty='" + someProperty + '\'' +
                '}';
    }
}