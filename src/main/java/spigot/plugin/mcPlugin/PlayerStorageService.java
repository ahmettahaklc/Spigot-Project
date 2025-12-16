package spigot.plugin.mcPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Steerable;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStorageService {
    private final StorageManager storageManager;
    private final Map<UUID, Inventory> openInventories = new HashMap<>();

    public PlayerStorageService(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void open(Player player) {
        UUID id = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory(player,InventoryType.CHEST,ChatColor.AQUA + "Player Custom Storage");
        Bukkit.getScheduler().runTaskAsynchronously(McPlugin.getInstance(),()->{
            String data = storageManager.loadPlayerStorage(id.toString());
            ItemStack[] contents = ItemSerializer.deserializeInventory(data);
            Bukkit.getScheduler().runTask(McPlugin.getInstance(), () -> {
                if (contents != null) {
                    inventory.setContents(contents);
                }
                openInventories.put(id,inventory);
                player.openInventory(inventory);
            });
        });
    }

    public void close(Player player) {
        UUID id = player.getUniqueId();
        Inventory inventory = openInventories.remove(id);

        if(inventory == null) return;

        //save items in database
        String items = ItemSerializer.serializeInventory(inventory.getContents());
        Bukkit.getScheduler().runTaskAsynchronously(McPlugin.getInstance(), () -> {
                    storageManager.savePlayerStorage(id.toString(), items);
        });
    }
}