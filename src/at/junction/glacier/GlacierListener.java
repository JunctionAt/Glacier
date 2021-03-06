package at.junction.glacier;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;


class GlacierListener implements Listener {

    Glacier plugin;

    public GlacierListener(Glacier plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        //Only moderators should have water/lava blocks. THIS IS NOT BUCKETS, ONLY WATER/LAVA BLOCK ITEMS.
        if (event.getBlock().isLiquid()) {
            if (plugin.frozenPlayers.contains(event.getPlayer().getName())) {
                plugin.newFrozen(event.getBlock());
            } else if (event.getPlayer().hasPermission("glacier.flowing")) {
                plugin.delFrozen(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        String world = event.getToBlock().getWorld().getName();
        Long from = plugin.hashLocation(event.getBlock().getLocation());
        Long to = plugin.hashLocation(event.getToBlock().getLocation());
        if (plugin.frozenBlocks.get(world).contains(from)) {
            //If block is frozen, CANCEL EVENT
            event.setCancelled(true);
        } else if (plugin.config.BLOCK_WATER_DESTROY_LIST.contains(event.getToBlock().getType())){
            //If TO is in blocked list, cancel event
            event.setCancelled(true);
        } else if (!plugin.canFlowInRegion(event.getBlock(), event.getToBlock())) {
            //If all regions in FROM do NOT match all regions in TO, cancel event
            event.setCancelled(true);
        } else if (plugin.frozenBlocks.get(world).contains(to)){
            plugin.delFrozen(event.getToBlock().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Material mat = event.getBlock().getType();

        if (mat == Material.STATIONARY_LAVA || mat == Material.STATIONARY_WATER) {
            String world = event.getBlock().getWorld().getName();
            Long hash = plugin.hashLocation(event.getBlock().getLocation());
            if (plugin.frozenBlocks.get(world).contains(hash)) {
                //If a block is frozen, do not allow it to flow.
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        //I *think* this stops ice melting, not 100% sure
        if (event.getBlock().getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Material mat = event.getItemStack().getType();
        if (mat == Material.LAVA_BUCKET || mat == Material.WATER_BUCKET) {
            plugin.delFrozen(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (plugin.config.BLOCK_WATER_DESTROY_LIST.contains(event.getBlockClicked().getType())){
            event.setCancelled(true);
        } else if (plugin.frozenPlayers.contains(event.getPlayer().getName())) {
            //If player is frozen, place frozen block
            plugin.newFrozen(event.getBlockClicked().getRelative(event.getBlockFace()));
        } else if (plugin.config.FREEZE_LAVA && event.getBucket() == Material.LAVA_BUCKET) {
            //If we always freeze lava, freeze it regardless of previous statements
            plugin.newFrozen(event.getBlockClicked().getRelative(event.getBlockFace()));
        } //Implied 'else flow'
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event){
        Player p = event.getPlayer();
        if ((p.getItemInHand().getType() == Material.LAVA_BUCKET) ||
                (p.getItemInHand().getType() == Material.WATER_BUCKET)){
            if (plugin.config.BLOCK_WATER_DESTROY_LIST.contains(event.getClickedBlock().getType())){
                event.setCancelled(true);
            }
        }
    }
}

