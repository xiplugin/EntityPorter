package com.coderxi.plugin.entityPorter.support;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.RanksManager;

import java.util.Locale;

public class BentoBoxSupport implements Support {

    private Plugin plugin;
    private Flag ENTITYPORTER_ISLAND_PROTECTION;

    @Override
    public boolean init(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().isPluginEnabled("BentoBox")) {
            ENTITYPORTER_ISLAND_PROTECTION = new Flag.Builder("ENTITYPORTER_ISLAND_PROTECTION", Material.IRON_HORSE_ARMOR)
                    .mode(Flag.Mode.BASIC)
                    .defaultRank(RanksManager.SUB_OWNER_RANK).build();
            for (Locale locale : BentoBox.getInstance().getLocalesManager().getLanguages().keySet()) {
                ENTITYPORTER_ISLAND_PROTECTION.setTranslatedName(locale, plugin.getConfig().getString("support.bentobox.flag-name"));
                ENTITYPORTER_ISLAND_PROTECTION.setTranslatedDescription(locale, plugin.getConfig().getString("support.bentobox.flag-description"));
            }
            BentoBox.getInstance().getFlagsManager().registerFlag(ENTITYPORTER_ISLAND_PROTECTION);
            return true;
        }
        return false;
    }

    public boolean checkPermission(Player player) {
        return BentoBox.getInstance().getIslandsManager()
                .getProtectedIslandAt(player.getLocation())
                .map(island -> island.isAllowed(User.getInstance(player), ENTITYPORTER_ISLAND_PROTECTION))
                .orElse(true);
    }

    @Override
    public String noPermissionMessage() {
        return plugin.getConfig().getString("support.bentobox.no-permission");
    }
}
