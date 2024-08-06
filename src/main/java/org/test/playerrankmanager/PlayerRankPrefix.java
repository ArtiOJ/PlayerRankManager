package org.test.playerrankmanager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.test.playerrankmanager.utilities.Colorize;

import java.util.*;

public class PlayerRankPrefix {

    private MongoServer database;
    private PlayerRankManager plugin;
    private List<Rank> ranks;
    private List<Override> overrides;

    public PlayerRankPrefix(PlayerRankManager plugin, MongoServer database) {
        this.plugin = plugin;
        this.database = database;
        this.ranks = new ArrayList<>();
        this.overrides = new ArrayList<>();
        loadRanks();

    }

    public void loadRanks() {
        FileConfiguration config = plugin.getConfig();
        List<Map<?, ?>> ranksConfig = config.getMapList("ranks");
        List<Map<?, ?>> overridesConfig = config.getMapList("overrides");

        for (Map<?, ?> rankConfig : ranksConfig) {
            String name = (String) rankConfig.get("name");
            String prefix = Colorize.format((String) rankConfig.get("prefix"));
            int xp = (int) rankConfig.get("xp");
            ranks.add(new Rank(name, prefix, xp));
        }
        ranks.sort(Comparator.comparingInt(Rank::getXp));

        for (Map<?, ?> overrideConfig : overridesConfig) {
            String name = (String) overrideConfig.get("name");
            String prefix = Colorize.format((String) overrideConfig.get("prefix"));
            overrides.add(new Override(name, prefix));
        }
    }

    public void reloadRank(Player player) {
        String uuid = player.getUniqueId().toString();
        String overrideRankName = database.getPlayerOverrideByUuid(uuid);
        Rank playerRank;

        if (overrideRankName != null) {
            Override overrideRank = getOverrideByName(overrideRankName);
            if (overrideRank != null) {
                player.setDisplayName(overrideRank.getPrefix() + player.getName());
                player.setPlayerListName(overrideRank.getPrefix() + player.getName());
                return;
            }
        }

        int xp = database.getXpByUuid(uuid);
        playerRank = getRankByXp(xp);
        player.setDisplayName(playerRank.getPrefix() + player.getName());
        player.setPlayerListName(playerRank.getPrefix() + player.getName());
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

    private Override getOverrideByName(String name) {
        for (Override override : overrides) {
            if (override.getName().equalsIgnoreCase(name)) {
                return override;
            }
        }
        return null;
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

    private static class Override {
        private final String name;
        private final String prefix;

        public Override(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
