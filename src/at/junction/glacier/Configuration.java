package at.junction.glacier;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

class Configuration {
    Glacier plugin;

    public boolean FREEZE_LAVA;
    public boolean ALLOW_TOGGLE;
    public boolean DEBUG;
    public List<Material> BLOCK_WATER_DESTROY_LIST;

    public Configuration(Glacier plugin) {
        this.plugin = plugin;
    }

    public void load() {
        BLOCK_WATER_DESTROY_LIST = new ArrayList<>();
        FREEZE_LAVA = plugin.getConfig().getBoolean("freeze-lava", true);
        ALLOW_TOGGLE = plugin.getConfig().getBoolean("allow-toggle", true);
        DEBUG = plugin.getConfig().getBoolean("debug", false);
        for (String mat : plugin.getConfig().getStringList("material-list")) {
            try {
                BLOCK_WATER_DESTROY_LIST.add(Material.getMaterial(mat));
            } catch (Exception e) {
                plugin.getLogger().warning(String.format("Block type %s not found", mat));
            }
        }
    }

    public void save() {

    }
}
