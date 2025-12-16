    package spigot.plugin.mcPlugin;
    import org.bukkit.ChatColor;
    import org.bukkit.command.Command;
    import org.bukkit.command.CommandExecutor;
    import org.bukkit.command.CommandSender;
    import org.bukkit.entity.Player;
    import org.bukkit.plugin.java.JavaPlugin;


    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;

    public final class McPlugin extends JavaPlugin implements CommandExecutor {
        private Connection connection;
        private String  url = "jdbc:mysql://localhost/my_plugin";
        private String  user = "root";
        private String password = "";
        private StorageManager storageManager;
        private static McPlugin instance;
        private PlayerStorageService playerStorageService;

        @Override
        public void onEnable() {
            instance = this;
            try {
                this.connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to database successfully.");
                this.storageManager = new StorageManager(connection);
                this.playerStorageService = new PlayerStorageService(storageManager);
                storageManager.CreateStorageTable();
                getServer().getPluginManager().registerEvents((new StorageListener(this.playerStorageService)), this);
                this.getCommand("depo").setExecutor(this);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to database.");
            }
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if(!command.getName().equalsIgnoreCase("depo")){
                return false;
            }

            if (!(sender instanceof Player player)){
                sender.sendMessage(ChatColor.RED + "This command has used for only players");
                return true;
            }
            this.playerStorageService.open(player);
            return true;
        }

        @Override
        public void onDisable() {
            try {
                this.connection.close();
                System.out.println("Disconnected from database successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to close connection to database.");
            }
        }

        public static McPlugin getInstance() {
            return instance;
        }
    }
