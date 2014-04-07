package at.junction.glacier;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.material.Dispenser;

class GlacierListener implements Listener {

    Glacier plugin;

    public GlacierListener(Glacier plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        //Only moderators should have water/lava blocks. THIS IS NOT BUCKETS, ONLY WATER/LAVA BLOCK ITEMS.
        if (event.getBlock().isLiquid()) {
            if (!(event.getPlayer().hasPermission("glacier.flowing") && !plugin.frozenPlayers.contains(event.getPlayer().getName()))) {
                plugin.newFrozen(event.getBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            if (!event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                plugin.newFrozen(event.getBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (plugin.frozenBlocks.get(event.getBlock().getWorld().getName()).contains(plugin.hashLocation(event.getBlock().getLocation()))) {
            //If FROM is frozen, cancel event
            event.setCancelled(true);
        } else if (!plugin.canFlowInRegion(event.getBlock(), event.getToBlock())) {
            //If all regions in FROM do NOT match all regions in TO, cancel event
            event.setCancelled(true);
        } else if (plugin.frozenBlocks.get(event.getToBlock().getLocation().getWorld().getName()).contains(plugin.hashLocation(event.getToBlock().getLocation()))) {
            //If TO is in frozenBlocks, remove TO from frozenBlocks and allow it to flow.
            plugin.delFrozen(event.getToBlock().getLocation());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Material mat = event.getBlock().getType();

        if (mat == Material.STATIONARY_LAVA || mat == Material.STATIONARY_WATER) {
            if (plugin.frozenBlocks.get(event.getBlock().getWorld().getName()).contains(plugin.hashLocation(event.getBlock().getLocation()))) {
                //If a block is frozen, do not allow it to flow.
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        Material mat = event.getItem().getType();
        if (mat == Material.LAVA_BUCKET || mat == Material.WATER_BUCKET) {
            Dispenser dispenser = (Dispenser) event.getBlock().getState().getData();
            plugin.newFrozen(event.getBlock().getRelative(dispenser.getFacing()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (plugin.config.DEBUG) {
            plugin.getLogger().info(String.format("Filled bucket at %s", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation()));
            plugin.getLogger().info(String.format("Hash %s", plugin.hashLocation(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())));

        }
        Material mat = event.getItemStack().getType();
        if (mat == Material.LAVA_BUCKET || mat == Material.WATER_BUCKET) {
            plugin.delFrozen(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (plugin.config.DEBUG) {
            plugin.getLogger().info(String.format("Empty bucket at %s", event.getBlockClicked().getRelative(event.getBlockFace())));
            plugin.getLogger().info(String.format("Hash %s", plugin.hashLocation(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())));
        }

        if (plugin.frozenPlayers.contains(event.getPlayer().getName())) {
            //If player is frozen, place frozen block
            plugin.newFrozen(event.getBlockClicked().getRelative(event.getBlockFace()));
        } else if (!event.getPlayer().hasPermission("glacier.flowing") && (!plugin.canPlaceFlowingLiquid(event.getBlockClicked().getRelative(event.getBlockFace()), event.getPlayer().getName()))) {
            //If player does not have permission AND cannot place a block, freeze it
            plugin.newFrozen(event.getBlockClicked().getRelative(event.getBlockFace()));
        } else if (plugin.config.FREEZE_LAVA && event.getBucket() == Material.LAVA_BUCKET) {
            //If we always freeze lava, freeze it regardless of previous statements
            plugin.newFrozen(event.getBlockClicked().getRelative(event.getBlockFace()));
        } //Implied 'else flow'
    }
}

