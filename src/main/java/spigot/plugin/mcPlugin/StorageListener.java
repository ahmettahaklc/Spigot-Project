package spigot.plugin.mcPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class StorageListener implements Listener {
    private final PlayerStorageService playerStorageService;


    public StorageListener(PlayerStorageService playerStorageService) {
        this.playerStorageService = playerStorageService;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(ChatColor.AQUA + "Player Custom Storage")) {
            if (event.getPlayer() instanceof Player player) {
                playerStorageService.close(player);
            }
        }
    }
}
