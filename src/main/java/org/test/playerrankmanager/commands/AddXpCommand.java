package org.test.playerrankmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.test.playerrankmanager.MongoServer;
import org.test.playerrankmanager.PlayerRankManager;

public class AddXpCommand implements CommandExecutor {

    private PlayerRankManager plugin;
    private MongoServer database;

    public AddXpCommand(PlayerRankManager plugin, MongoServer database) {
        this.plugin = plugin;
        this.database = database;

        plugin.getCommand("addxp").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /addxp <player> <amount>");
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

            // Add the specified XP to the player's current XP
            database.addXp(uuid, amount);

            sender.sendMessage("Added " + amount + " XP to " + playerName);
            player.sendMessage("You have been given " + amount + " XP.");
        } catch (NumberFormatException e) {
            sender.sendMessage("Amount must be a valid integer.");
            return false;
        }

        return true;
    }
}
