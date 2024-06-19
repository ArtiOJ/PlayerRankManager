package org.test.playerrankmanager.utilities;

import org.bukkit.ChatColor;

public class Colorize {

    public static String format(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
