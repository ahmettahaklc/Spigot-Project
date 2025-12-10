package spigot.plugin.myFirstPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;


public class StorageListener implements Listener {

    private final StorageManager storageManager;

    public StorageListener(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory closedInventory = event.getInventory();


        if (closedInventory.getHolder() != null && closedInventory.getHolder() instanceof Player) {

            if (event.getView().getTitle().equals(ChatColor.GOLD + "Kişisel Depo")) {

                // 2. Oyuncuyu al
                Player player = (Player) event.getPlayer();

                // 3. Envanteri Base64'e serileştir ve ASENKRON olarak kaydet
                try {
                    String base64Data = ItemSerializer.inventoryToBase64(closedInventory);

                    // Veritabanı işlemleri sunucuyu yavaşlatır, bu yüzden ASENKRON çalıştırılır
                    MyFirstPlugin.instance.getServer().getScheduler().runTaskAsynchronously(MyFirstPlugin.instance, () -> {
                        storageManager.saveInventory(player.getUniqueId().toString(), base64Data);
                        player.sendMessage(ChatColor.GREEN + "Depo içeriğiniz veritabanına kaydedildi!");
                    });

                } catch (IllegalStateException e) {
                    player.sendMessage(ChatColor.RED + "Hata: Depo içeriği kaydedilemedi.");
                    e.printStackTrace();
                }
            }
        }
    }
}