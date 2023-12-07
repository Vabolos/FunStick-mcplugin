package fun.funstick;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class FunStickPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.STICK && item.hasItemMeta()
                && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()
                && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("funstick")) {
            spawnRandomSheep(player);
        }
    }

    private void spawnRandomSheep(Player player) {
        Random random = new Random();
        EntityType[] colors = { EntityType.SHEEP, EntityType.SHEEP, EntityType.SHEEP,
                EntityType.SHEEP, EntityType.SHEEP, EntityType.SHEEP,
                EntityType.SHEEP, EntityType.SHEEP, EntityType.SHEEP };
        EntityType selectedColor = colors[random.nextInt(colors.length)];

        Sheep sheep = (Sheep) player.getWorld().spawnEntity(player.getLocation(), selectedColor);
        sheep.setAdult();
        sheep.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1)); // Making sheep invisible for 10 seconds
    }
}

