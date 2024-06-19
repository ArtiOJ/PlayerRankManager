package org.test.playerrankmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.test.playerrankmanager.MongoServer;
import org.test.playerrankmanager.PlayerRankManager;

public class GetXpCommand implements CommandExecutor {

    private PlayerRankManager plugin;
    private MongoServer database;

    public GetXpCommand(PlayerRankManager plugin, MongoServer database) {
        this.plugin = plugin;
        this.database = database;

        plugin.getCommand("getxp").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            sender.sendMessage("Usage: /getxp [player]");
            return false;
        }

        Player player;
        String playerName;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command without specifying a player.");
                return false;
            }
            player = (Player) sender;
            playerName = player.getName();
        } else {
            playerName = args[0];
            player = sender.getServer().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage("Player not found.");
                return false;
            }
        }

        String uuid = player.getUniqueId().toString();

        // Fetch the player's current XP
        int currentXp = database.getXpByUuid(uuid);

        if (args.length == 0) {
            player.sendMessage("You currently have " + currentXp + " XP.");
        } else {
            sender.sendMessage(playerName + " currently has " + currentXp + " XP.");
            if (!sender.equals(player)) {
                player.sendMessage("You currently have " + currentXp + " XP.");
            }
        }

        return true;
    }
}

