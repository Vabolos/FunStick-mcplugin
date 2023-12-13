package fun.funstick;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class FunStick extends JavaPlugin implements Listener {
    private boolean shootSheep = true; // Variable to toggle shooting behavior

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

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                shootSheep = !shootSheep; // Toggle shooting behavior
                if (shootSheep) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "FunStick" + ChatColor.GRAY + "] " + ChatColor.WHITE + "Switched to shooting sheep!");
                } else {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "FunStick" + ChatColor.GRAY + "] " + ChatColor.WHITE + "Switched to shooting particles!");
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f); // Play button click sound
                event.setCancelled(true); // Cancel the event to avoid using the "funstick" when toggling
            } else {
                launchSheep(player);
            }
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
        if (!shootSheep) {
            double trailLength = 100.0; // Length of the trail
            double particleInterval = 0.3; // Interval between particles

            // Create a wider, faster, and longer trail of redstone particles
            for (double t = 0; t < trailLength; t += particleInterval) {
                double x = player.getEyeLocation().getX() + t * player.getLocation().getDirection().getX();
                double y = player.getEyeLocation().getY() + t * player.getLocation().getDirection().getY() - 0.5; // Lower the Y-coordinate
                double z = player.getEyeLocation().getZ() + t * player.getLocation().getDirection().getZ();

                // Redstone's particles (white and aqua)
                player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 1, 0, 0, 0, 1,
                        new Particle.DustOptions(Color.WHITE, 6)); // White color

                player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 1, 0, 0, 0, 1,
                        new Particle.DustOptions(Color.AQUA, 6)); // Aqua color

                // Enchantment table particles
                player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, new Location(player.getWorld(), x, y, z), 1, 0, 0, 0, 0);
            }

            // Add flame effect moving outward
            Location playerLocation = player.getLocation();
            for (int i = 0; i < 10; i++) {
                double radius = i * 0.5;
                for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 12) {
                    double deltaX = radius * Math.cos(theta);
                    double deltaZ = radius * Math.sin(theta);
                    Location flameLocation = playerLocation.clone().add(deltaX, 0, deltaZ);
                    player.getWorld().spawnParticle(Particle.FLAME, flameLocation, 1, 0, 0, 0, 0);
                }
            }

            // Play sound effects
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
        } else {
            // Your existing sheep shooting logic
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
                            new Particle.DustOptions(Color.BLACK, 2)); // Larger black trail particles

                    // Additional larger particle trail for enchantment effect
                    sheep.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, sheep.getLocation(), 100, 0.5, 0.5, 0.5, 0.3);

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
}
