package ferrin.dupe.listener;

import ferrin.dupe.FerrinDupe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnvilImpactListener implements Listener {

    private final FerrinDupe plugin;
    private final Map<Location, UUID> activeShulkerViewers = new HashMap<>();

    public AnvilImpactListener(FerrinDupe plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOpenShulker(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof ShulkerBox shulker) {
            activeShulkerViewers.put(shulker.getLocation(), event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCloseShulker(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof ShulkerBox shulker) {
            activeShulkerViewers.remove(shulker.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilLand(EntityBlockFormEvent event) {
        if (!(event.getEntity() instanceof FallingBlock fallingBlock)) return;
        if (fallingBlock.getBlockData().getMaterial() != Material.ANVIL) return;

        Block targetBlock = event.getBlock();
        Block blockBelow = targetBlock.getRelative(0, -1, 0);

        if (!Tag.SHULKER_BOXES.isTagged(blockBelow.getType())) return;

        UUID viewerUUID = activeShulkerViewers.get(blockBelow.getLocation());
        if (viewerUUID == null) return;

        Player player = plugin.getServer().getPlayer(viewerUUID);
        if (player == null || !player.hasPermission("ferrindupe.use")) return;

        int cooldownSec = plugin.getConfig().getInt("cooldown-seconds");
        if (!plugin.getCooldownManager().isOffCooldown(player.getUniqueId(), cooldownSec)) return;

        double spawnY = fallingBlock.getOrigin() != null ? fallingBlock.getOrigin().getY() : targetBlock.getY();
        int minHeight = plugin.getConfig().getInt("min-anvil-height");
        if ((spawnY - targetBlock.getY()) < minHeight) return;

        double chance = plugin.getConfig().getDouble("success-chance");
        if (Math.random() * 100 <= chance) {
            executeDuplication(player, blockBelow);
            plugin.getCooldownManager().setCooldown(player.getUniqueId());
        }
    }

    private void executeDuplication(Player player, Block shulkerBlock) {
        if (!(shulkerBlock.getState() instanceof ShulkerBox shulker)) return;
        
        Inventory sInv = shulker.getInventory();
        Inventory pInv = player.getInventory();

        for (ItemStack item : sInv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                pInv.addItem(item.clone());
            }
        }
    }
}