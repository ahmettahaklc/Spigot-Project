package spigot.plugin.mcPlugin;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class StorageManager {

    private final Connection connection;
    Statement statement;

    public StorageManager(Connection connection) {
        this.connection = connection;
    }

    public void CreateStorageTable(){
        try {
            this.statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS player_storage (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "inventory_data LONGTEXT NOT NULL" +
                    ");";
            statement.execute(sql);
            System.out.println("Successfully created player storage");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerStorage(String uuid, String inventory_data){
        String sql = "INSERT INTO player_storage (uuid, inventory_data) VALUES (?, ?)" + "ON DUPLICATE KEY UPDATE inventory_data = VALUES(inventory_data);";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, inventory_data);
            ps.executeUpdate();
            System.out.println("Player storage saved/updated for UUID: " + uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public String loadPlayerStorage(String uuid){
        String sql = "SELECT inventory_data FROM player_storage WHERE uuid = ?;";

        try(PreparedStatement ps = connection.prepareStatement(sql) ) {
            ps.setString(1, uuid);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("inventory_data");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
