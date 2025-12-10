package spigot.plugin.myFirstPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MyFirstPlugin extends JavaPlugin implements CommandExecutor {
    private Connection connection;
    private StorageManager storageManager;
    public static MyFirstPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        String url = "jdbc:mysql://localhost/my_plugin";
        String user = "root";
        String password = "";


        try {
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully");

            this.storageManager = new StorageManager(this.connection);
            this.storageManager.createStorageTable();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database");
            return;
        }
        getServer().getPluginManager().registerEvents(new StorageListener(this.storageManager), this);
        /**
        try {
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS players_stats(deaths int, kills int,blocks_broken long, balance double, last_login DATE, last_logout DATE)";
            statement.execute(sql);

            statement.close();
            connection.close();


            System.out.println("Created the stats table in database ");
        } catch (SQLException e) {
            e.printStackTrace();

            System.out.println("Failed to create the stats table in database");
        }
**/
        this.getCommand("depo").setExecutor(this);

        getLogger().info("Depo komutu başarıyla kaydedildi!");

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("depo")) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can execute this command");
                return true;
            }

            // 1. Envanterin başlığı (Listener'da kontrol edilen ad)
            String inventoryTitle = ChatColor.GOLD + "Kişisel Depo";

            // 2. Boş envanteri oluşturun (null yerine oyuncu sahibi yapın)
            Inventory storage = Bukkit.createInventory(player, 54, inventoryTitle); // 54 yuva önerilir.

            player.sendMessage(ChatColor.YELLOW + "Depo yükleniyor...");

            // 3. Eşyaları ASENKRON olarak yükleyin
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                // StorageManager'dan Base64 veriyi çek
                String base64Data = storageManager.loadInventory(player.getUniqueId().toString());

                // Yükleme tamamlanınca, eşyaları yerleştirmek ve menüyü açmak için ANA DÖNGÜYE DÖN
                getServer().getScheduler().runTask(this, () -> {
                    if (base64Data != null) {
                        try {
                            // Base64 veriyi çözün ve envantere yerleştirin
                            ItemStack[] loadedItems = ItemSerializer.inventoryFromBase64(base64Data);
                            storage.setContents(loadedItems);
                            player.sendMessage(ChatColor.GREEN + "Depo içeriğiniz yüklendi!");

                        } catch (Exception e) {
                            player.sendMessage(ChatColor.RED + "Hata: Depo içeriği çözülemedi.");
                            getLogger().log(Level.SEVERE, "Failed to deserialize inventory for " + player.getName(), e);
                        }
                    }

                    // 4. Envanteri Oyuncuya Açın
                    player.openInventory(storage);
                });
            });

            return true;
        }
        return false;
    }
    /**
      @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (command.getName().equalsIgnoreCase("depo")) {


      if (!(sender instanceof Player player)) {
      sender.sendMessage("Only players can execute this command");
      return true;
      }


      String inventoryTitle = "GUI Depo";
      Inventory storage = Bukkit.createInventory(player, InventoryType.CHEST, ChatColor.AQUA + inventoryTitle);


      player.openInventory(storage);
      player.sendMessage("Depo GUI opened successfully.");

      return true;
      }
      return false;
      }

    **/

    @Override
    public void onDisable() {
        getLogger().info("Plugin has been disabled!");
        if (this.connection != null) {
            try {
                this.connection.close();
                getLogger().info("Database connection closed.");
            } catch (SQLException e) {
                getLogger().severe("Failed to close database connection.");
            }
        }
    }
}
