package org.test.playerrankmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.test.playerrankmanager.MongoServer;
import org.test.playerrankmanager.PlayerRankManager;
import org.test.playerrankmanager.PlayerRankPrefix;

public class ReloadRankCommand implements CommandExecutor {

    private PlayerRankManager plugin;
    private PlayerRankPrefix rankManager;

    public ReloadRankCommand(PlayerRankManager plugin, PlayerRankPrefix rankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;

        plugin.getCommand("reloadrank").setExecutor(this);
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
        } else {
            playerName = args[0];
            player = sender.getServer().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage("Player not found.");
                return false;
            }
        }

        rankManager.reloadRank(player);

        return true;
    }
}
