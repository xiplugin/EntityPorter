package com.coderxi.plugin.entityPorter.support;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface Support {
    boolean init(Plugin plugin);
    boolean checkPermission(Player player);
    String noPermissionMessage();
}
