package org.test.playerrankmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.test.playerrankmanager.MongoServer;
import org.test.playerrankmanager.PlayerRankManager;

public class RemoveOverrideCommand implements CommandExecutor {

    private PlayerRankManager plugin;
    private MongoServer database;
    public RemoveOverrideCommand(PlayerRankManager plugin, MongoServer database) {
        this.plugin = plugin;
        this.database = database;

        plugin.getCommand("removerank").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /removeoverride <player>");
            return false;
        }

        String playerName = args[0];
        Player player = sender.getServer().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        String uuid = player.getUniqueId().toString();

        database.removePlayerOverrideByUuid(uuid);

        sender.sendMessage(playerName + "'s rank has been revoked.");
        player.sendMessage("Your rank has been revoked.");

        return true;
    }
}