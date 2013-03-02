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
    private String world;
    
    @NotNull
    private double x, y, z;
    
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setWorld(String world) {
        this.world = world;
    }
    
    public String getWorld() {
        return world;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getX() {
        return x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getY() {
        return y;
    }
    
    public void setZ(double z) {
        this.z = z;
    }
    
    public double getZ() {
        return z;
    }
}