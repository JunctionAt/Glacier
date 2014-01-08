package at.junction.glacier.database;

import com.avaje.ebean.validation.NotNull;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "liquids")


//Note - Liquid stores X/Y/Z coords and glacier rehashes them upon load
//Leave it this way - do NOT store the hash
//We may want to change the hashing method later on
public class Liquid implements Serializable {

    @Id
    private int id;

    @NotNull
    private int x, y, z;

    @NotNull
    private String world;

    public Liquid() {
    }

    public Liquid(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getZ() {
        return z;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getWorld() {
        return world;
    }
}