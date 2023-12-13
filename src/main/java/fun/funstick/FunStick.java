package fun.funstick;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class FunStick extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("FunStick is running!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FunStick is shutting down!");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.BLAZE_ROD && item.hasItemMeta()
                && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()
                && ChatColor.stripColor(Objects.requireNonNull(item.getItemMeta().getDisplayName())).equalsIgnoreCase("funstick")) {

            launchSheep(player);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            ItemStack item = event.getItem().getItemStack();

            if (item.getType() == Material.BLAZE_ROD && item.hasItemMeta()
                    && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()
                    && ChatColor.stripColor(Objects.requireNonNull(item.getItemMeta().getDisplayName())).equalsIgnoreCase("funstick")) {

                sendAnnouncementMessage(player);
                addEnchantmentAndGlow(item); // Enchant the stick when picked up
            }
        }
    }

    private void sendAnnouncementMessage(Player player) {
        String message = ChatColor.translateAlternateColorCodes('&', "&l&dCongratulations! You've acquired the &oFunstick!");
        player.sendTitle("", message, 10, 100, 20); // Adjusted title size to 100
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f); // Play the XP sound
    }

    private void addEnchantmentAndGlow(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(itemMeta);
            item.addUnsafeEnchantment(Enchantment.LUCK, 1); // Adding glow effect
        }
    }

    private void launchSheep(Player player) {
        Sheep sheep = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)), Sheep.class);
        sheep.setVelocity(player.getLocation().getDirection().multiply(4)); // Original velocity

        // Play original shooting effects
        player.getWorld().spawnParticle(Particle.FLAME, sheep.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().spawnParticle(Particle.CLOUD, sheep.getLocation(), 30, 0.2, 0.2, 0.2, 0.1);
        player.getWorld().spawnParticle(Particle.CRIT, sheep.getLocation(), 20, 0.3, 0.3, 0.3, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);

        // Custom movement loop for the sheep
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!sheep.isDead()) {
                sheep.getWorld().spawnParticle(Particle.REDSTONE, sheep.getLocation(), 20, 0.2, 0.2, 0.2, 0.5,
                        new Particle.DustOptions(Color.BLACK, 1)); // Larger black trail particles

                // Additional larger particle trail for enchantment effect
                sheep.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, sheep.getLocation(), 20, 0.5, 0.5, 0.5, 0.3);

                Block block = sheep.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (block.getType() != Material.AIR) { // Check if the block below the sheep is solid
                    sheep.getWorld().createExplosion(sheep.getLocation(), 5.0f, false, true, player); // Larger explosion

                    // Add a larger particle effect with brighter colors
                    sheep.getWorld().spawnParticle(Particle.REDSTONE, sheep.getLocation(), 500, 2, 2, 2, 1,
                            new Particle.DustOptions(Color.BLACK, 1)); // Black particles
                    sheep.getWorld().spawnParticle(Particle.REDSTONE, sheep.getLocation(), 500, 2, 2, 2, 1,
                            new Particle.DustOptions(Color.AQUA, 1)); // Cyan particles
                    sheep.getWorld().spawnParticle(Particle.REDSTONE, sheep.getLocation(), 500, 2, 2, 2, 1,
                            new Particle.DustOptions(Color.BLUE, 1)); // Blue particles

                    sheep.getWorld().playSound(sheep.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 2.0f, 1.0f); // Increased fireworks sound volume
                    sheep.remove(); // Remove the sheep after the impact effects
                }
            }
        }, 0L, 1L); // Check for movement and impact continuously (every tick)
    }
}

