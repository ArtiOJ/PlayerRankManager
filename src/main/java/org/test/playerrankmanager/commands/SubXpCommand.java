package org.test.playerrankmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.test.playerrankmanager.MongoServer;
import org.test.playerrankmanager.PlayerRankManager;

public class SubXpCommand implements CommandExecutor {

    private PlayerRankManager plugin;
    private MongoServer database;

    public SubXpCommand(PlayerRankManager plugin, MongoServer database) {
        this.plugin = plugin;
        this.database = database;

        plugin.getCommand("subxp").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /subxp <player> <amount>");
            return false;
        }

        String playerName = args[0];
        Player player = sender.getServer().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount < 0) {
                sender.sendMessage("Amount must be a positive integer.");
                return false;
            }

            String uuid = player.getUniqueId().toString();

            // Fetch the player's current XP
            Integer currentXp = database.getXpByUuid(uuid);
            if (currentXp == null) {
                sender.sendMessage("Player not found in the database.");
                return false;
            }

            // Calculate the new XP value
            int newXp = currentXp - amount;
            if (newXp < 0) {
                sender.sendMessage("Player's XP cannot be negative.");
                return false;
            }

            // Update the player's XP in the database
            database.updateXp(uuid, newXp);

            sender.sendMessage("Subtracted " + amount + " XP from " + playerName);
            player.sendMessage("You have lost " + amount + " XP.");
        } catch (NumberFormatException e) {
            sender.sendMessage("Amount must be a valid integer.");
            return false;
        }

        return true;
    }
}
