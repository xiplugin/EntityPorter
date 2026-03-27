package com.coderxi.plugin.entityPorter.core;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor(staticName = "of")
public class Porter {

    private static final Map<UUID, Entity> liftedEntitiesMap = Maps.newConcurrentMap();

    private final Player player;
    public void lift(Entity e, boolean createArmorStand) {
        Entity entity = createArmorStand ? player.getWorld().spawn(player.getLocation(), ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setGravity(false);
            armorStand.setBasePlate(false);
            armorStand.addPassenger(e);
        }): e;
        player.addPassenger(entity);
        liftedEntitiesMap.put(player.getUniqueId(), entity);
    }

    public void drop() {
        Entity e = liftedEntitiesMap.get(player.getUniqueId());
        if (e instanceof ArmorStand) {
            e.getPassengers().forEach(e::removePassenger);
            e.remove();
        } else {
            try {
                player.removePassenger(e);
            } catch (Exception ignore) {}
        }
        liftedEntitiesMap.remove(player.getUniqueId());
    }

}
