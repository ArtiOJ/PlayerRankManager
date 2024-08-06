package org.test.playerrankmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.test.playerrankmanager.MongoServer;
import org.test.playerrankmanager.PlayerRankManager;
import org.test.playerrankmanager.PlayerRankPrefix;

import java.util.List;
import java.util.Map;

public class PromoteCommand implements CommandExecutor {
    private PlayerRankManager plugin;
    private MongoServer database;
    private FileConfiguration config;

    public PromoteCommand(PlayerRankManager plugin, MongoServer database) {
        this.plugin = plugin;
        this.database = database;

        config = plugin.getConfig();

        plugin.getCommand("promote").setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2 || args.length < 2) {
            sender.sendMessage("Usage: /promote <player> <rank>");
            return false;
        }

        String playerName = args[0];
        Player player = sender.getServer().getPlayer(playerName);

        String rank = args[1];
        List<Map<?, ?>> ranksConfig = config.getMapList("overrides");

        boolean found = false;
        for (Map<?, ?> rankConfig : ranksConfig) {
            String name = (String) rankConfig.get("name");
            if (name.equalsIgnoreCase(rank)) {
                found = true;
            }
        }
        if (!found) {
            player.sendMessage("The rank '" + rank + "' is not valid.");
            return false;
        }


        if (player == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        String uuid = player.getUniqueId().toString();

        // Fetch the player's current XP
        database.addPlayerOverrideByUuid(uuid, rank);

        sender.sendMessage(playerName + " has been promoted to the " + rank + " rank!");

        player.sendMessage("You have been promoted to the " + rank + " rank, congratulations!");


        return true;
    }
}
