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
        if(event.getBlock().isLiquid()) { // If the block is liquid.
            if(plugin.config.FREEZE_LAVA && event.getBlock().getType() == Material.LAVA && !event.getPlayer().hasPermission("glacier.flowing")) { // If we have lava set to simply freeze.
                plugin.newFrozen(event.getBlock());
                return;
            }
            
            if(plugin.frozenPlayers.contains(event.getPlayer().getName()) || (!plugin.hasRegion(event.getBlock()) || !plugin.isBlockRegionMember(event.getBlock(), event.getPlayer().getName())) && !event.getPlayer().hasPermission("glacier.flowing")) { // If the player doesn't have the permission or isn't a member of the regions.
                plugin.newFrozen(event.getBlock());
            } else { // If the player DOES have the permission and is a member of the regions.
                if(plugin.frozenBlocks.contains(event.getBlock().getLocation())) {
                    plugin.delFrozen(event.getBlock().getLocation());
                }
            }
        } else { // If the block is not liquid, it might be replacing an old liquid. Check.
            if(plugin.frozenBlocks.contains(event.getBlock().getLocation())) {
                plugin.delFrozen(event.getBlock().getLocation());
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
        if(plugin.frozenBlocks.contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }
        
        if(!plugin.canFlowInRegion(event.getBlock(), event.getToBlock())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Material mat = event.getBlock().getType();

        if(mat == Material.STATIONARY_LAVA || mat == Material.STATIONARY_WATER) {
            if(plugin.frozenBlocks.contains(event.getBlock().getLocation())) {
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
            Dispenser dispenser = (Dispenser)event.getBlock().getState().getData();
            plugin.newFrozen(event.getBlock().getRelative(dispenser.getFacing()));
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
        if(plugin.config.FREEZE_LAVA && event.getPlayer().getItemInHand().getType() == Material.LAVA_BUCKET && !event.getPlayer().hasPermission("glacier.flowing")) { // If we have lava set to simply freeze.
            plugin.newFrozen(event.getBlockClicked().getRelative(event.getBlockFace()));
            return;
        }
        
        if(plugin.frozenPlayers.contains(event.getPlayer().getName()) || (!plugin.hasRegion(event.getBlockClicked().getRelative(event.getBlockFace())) || !plugin.isBlockRegionMember(event.getBlockClicked().getRelative(event.getBlockFace()), event.getPlayer().getName())) && !event.getPlayer().hasPermission("glacier.flowing")) {
            plugin.newFrozen(event.getBlockClicked().getRelative(event.getBlockFace()));
        } else {
            if(plugin.frozenBlocks.indexOf(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation()) != -1) {
                plugin.delFrozen(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
            }
        }
    }
}
