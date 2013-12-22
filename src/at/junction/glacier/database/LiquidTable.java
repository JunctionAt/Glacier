package at.junction.glacier.database;

import at.junction.glacier.Glacier;
import com.avaje.ebean.Query;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class LiquidTable {
    private final Glacier plugin;
    
    public LiquidTable(Glacier plugin) {
        this.plugin = plugin;
    }
    
    void save(Liquid liquid) {
        plugin.getDatabase().save(liquid);
    }
    
    public void newFrozen(String world, int x, int y, int z) {
        Liquid frozen = new Liquid();
        frozen.setWorld(world);
        frozen.setX(x);
        frozen.setY(y);
        frozen.setZ(z);
        save(frozen);
    }
    
    public void delFrozen(String world, int x, int y, int z) {
        Query<Liquid> query = plugin.getDatabase().find(Liquid.class).where().eq("world", world).eq("x", x).eq("y", y).eq("z", z).setMaxRows(1);
        plugin.getDatabase().delete(query.findUnique());
    }
    
    public ArrayList<Location> getFrozen() {
        ArrayList<Location> ret = new ArrayList<>();
        List<Liquid> query = plugin.getDatabase().find(Liquid.class).findList();
        
        for(Liquid l : query) {
            Location loc = new Location(plugin.getServer().getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
            ret.add(loc);
        }
        
        return ret;
    }
}