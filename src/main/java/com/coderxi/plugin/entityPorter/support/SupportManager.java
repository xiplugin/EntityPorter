package com.coderxi.plugin.entityPorter.support;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.List;

public class SupportManager {

    @Getter
    private final List<Support> activeSupports = List.of(
            new BentoBoxSupport()
    );

    public void init(Plugin plugin) {
        activeSupports.forEach(support -> {
            try {
                if (support.init(plugin)) plugin.getLogger().info("Support Loaded:" + support.getClass().getSimpleName());
            } catch (Exception e) {
                plugin.getLogger().warning("Support Load Failed: " + support.getClass().getSimpleName());
            }
        });
    }

    public boolean checkAllSupportsPermission(Player player) {
        for (Support support : activeSupports) {
            if (!support.checkPermission(player)){
                player.sendMessage(MiniMessage.miniMessage().deserialize(support.noPermissionMessage()));
                return false;
            }
        }
        return true;
    }

}