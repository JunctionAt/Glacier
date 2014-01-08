package at.junction.glacier.database;

import at.junction.glacier.Glacier;
import com.avaje.ebean.Query;

import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;

public class LiquidTable {
    Glacier plugin;

    public LiquidTable(Glacier plugin) {
        this.plugin = plugin;
    }

    public void save(Liquid liquid) {
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

    public Map<String, HashSet<Long>> getFrozen() {
        Map<String, HashSet<Long>> frozenBlocks = new HashMap<>();
        List<Liquid> query = plugin.getDatabase().find(Liquid.class).findList();

        for (World w : plugin.getServer().getWorlds()) {
            frozenBlocks.put(w.getName(), new HashSet<Long>());
        }

        for (Liquid l : query) {
            frozenBlocks.get(l.getWorld()).add(plugin.hashLocation(l.getX(), l.getY(), l.getZ()));
        }

        return frozenBlocks;
    }
}