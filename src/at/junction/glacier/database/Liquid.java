package at.junction.glacier.database;

import com.avaje.ebean.validation.NotNull;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "liquids")

public class Liquid implements Serializable {

    @Id
    private int id;
    
    @NotNull
    private int x, y, z;
    
    @NotNull
    private String world;
    
    
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