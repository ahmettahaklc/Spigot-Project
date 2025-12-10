package spigot.plugin.myFirstPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StorageManager {

    private final Connection connection;

    public StorageManager(Connection connection) {
        this.connection = connection;
    }

    public void createStorageTable() throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS player_storage (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "inventory_data LONGTEXT NOT NULL" +
                        ");"
        )) {
            statement.execute();
            MyFirstPlugin.instance.getLogger().info("Created the player_storage table.");
        }
    }


    public void saveInventory(String uuid, String base64Data) {
        // Prepared Statements kullandık (SQL Injection'a karşı güvenlik)
        String sql = "INSERT INTO player_storage (uuid, inventory_data) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE inventory_data = VALUES(inventory_data)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, base64Data);
            statement.executeUpdate();
        } catch (SQLException e) {
            MyFirstPlugin.instance.getLogger().severe("Failed to save inventory for UUID: " + uuid);
            e.printStackTrace();
        }
    }


    public String loadInventory(String uuid) {
        String sql = "SELECT inventory_data FROM player_storage WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("inventory_data");
                }
            }
        } catch (SQLException e) {
            MyFirstPlugin.instance.getLogger().severe("Failed to load inventory for UUID: " + uuid);
            e.printStackTrace();
        }
        return null;
    }
}