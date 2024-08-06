package org.test.playerrankmanager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.test.playerrankmanager.commands.*;
//import org.test.playerrankmanager.utilities.Colorize;

public final class PlayerRankManager extends JavaPlugin implements Listener {

    private MongoServer database;
    private PlayerRankPrefix rankManager;
    private String connection_string = "<private_connection_string>";
    private String dbname = "The_Limaverse_Data";
    private String collectionName = "Player_Data";


    @Override
    public void onEnable() {
        // Plugin startup logic
        database = new MongoServer(connection_string, dbname, collectionName);
        rankManager = new PlayerRankPrefix(this, database);
        new SetXpCommand(this, database);
        new AddXpCommand(this, database);
        new SubXpCommand(this, database);
        new GetXpCommand(this, database);
        new PromoteCommand(this, database);
        new RemoveOverrideCommand(this, database);
        new ReloadRankCommand(this, rankManager);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String name = event.getPlayer().getName();

        if (database.fetchDocumentByUuid(uuid) == null) {
            database.addDocument(uuid, 0, name);
        }
        rankManager.reloadRank(event.getPlayer());
    }
}

