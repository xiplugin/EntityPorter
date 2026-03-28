package com.coderxi.plugin.entityPorter.core;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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

    public Entity drop() {
        Entity e = liftedEntitiesMap.get(player.getUniqueId());
        if (e == null) e = player.getPassengers().isEmpty() ? null : player.getPassengers().getFirst();
        if (e == null) return null;
        if (e instanceof ArmorStand armorStand) {
            for(Entity p : armorStand.getPassengers()) {if (armorStand.removePassenger(e)) e = p;}
            armorStand.remove();
        } else {
            try { player.removePassenger(e); } catch (Exception ignore) {}
        }
        liftedEntitiesMap.remove(player.getUniqueId());
        return e;
    }

    public void toss() {
        Entity e = drop();
        Vector velocity = player.getLocation().getDirection().multiply(1.5).setY(0.5);
        e.setVelocity(velocity);
    }

    public void place(Block clickedBlock, BlockFace face) {
        Entity e = drop();
        Block targetBlock = clickedBlock.getRelative(face);
        Location dropLoc = targetBlock.getLocation().add(0.5, 0.1, 0.5);
        dropLoc.setYaw(player.getLocation().getYaw());
        e.teleport(dropLoc);
        e.setVelocity(new Vector(0, 0, 0));
    }

}
