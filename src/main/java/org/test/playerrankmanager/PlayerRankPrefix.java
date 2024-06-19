package org.test.playerrankmanager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.test.playerrankmanager.utilities.Colorize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PlayerRankPrefix implements Listener {

    private MongoServer database;
    private PlayerRankManager plugin;
    private List<Rank> ranks;

    public PlayerRankPrefix(PlayerRankManager plugin, MongoServer database) {
        this.plugin = plugin;
        this.database = database;
        this.ranks = new ArrayList<>();
        loadRanks();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void loadRanks() {
        FileConfiguration config = plugin.getConfig();
        List<Map<?, ?>> ranksConfig = config.getMapList("ranks");

        for (Map<?, ?> rankConfig : ranksConfig) {
            String name = (String) rankConfig.get("name");
            String prefix = Colorize.format((String) rankConfig.get("prefix"));
            int xp = (int) rankConfig.get("xp");
            ranks.add(new Rank(name, prefix, xp));
        }
        ranks.sort(Comparator.comparingInt(Rank::getXp));
    }

    public void reloadRank(Player player) {
        String uuid = player.getUniqueId().toString();

        int xp = database.getXpByUuid(uuid);
        Rank playerRank = getRankByXp(xp);
        player.setDisplayName(playerRank.getPrefix() + player.getName());
        player.setPlayerListName(playerRank.getPrefix() + player.getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        reloadRank(event.getPlayer());
    }

    private Rank getRankByXp(int xp) {
        Rank rank = ranks.get(0);

        for (Rank r : ranks) {
            if (xp >= r.getXp()) {
                rank = r;
            } else {
                break;
            }
        }
        return rank;
    }

    private static class Rank {
        private final String name;
        private final String prefix;
        private final int xp;

        public Rank(String name, String prefix, int xp) {
            this.name = name;
            this.prefix = prefix;
            this.xp = xp;
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }

        public int getXp() {
            return xp;
        }
    }
}


