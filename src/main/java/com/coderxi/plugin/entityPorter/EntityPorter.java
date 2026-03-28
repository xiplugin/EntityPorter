package com.coderxi.plugin.entityPorter;

import com.coderxi.plugin.entityPorter.core.Porter;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EntityPorter extends JavaPlugin implements Listener {

    private boolean enableOnJoin = true;
    private boolean allowLooting = true;
    private boolean createArmorStand = false;
    private final Set<String> enabledWorlds = new HashSet<>();
    private boolean allAnimalsLiftable = false;
    private Set<EntityType> liftableEntities;
    private final Set<UUID> porters = ConcurrentHashMap.newKeySet();


    @Override
    public void onEnable() {
        saveDefaultConfig();
        onReload(true);
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void onReload(boolean init) {
        if (!init) {
            reloadConfig();
            enabledWorlds.clear();
        }
        Configuration config = getConfig();
        enableOnJoin = config.getBoolean("enable-on-join");
        allowLooting = config.getBoolean("allow-looting");
        createArmorStand = config.getBoolean("create-armor-stand");
        enabledWorlds.addAll(config.getStringList("enabled-worlds"));
        List<String> liftableEntitieList = config.getStringList("liftable-entities");
        allAnimalsLiftable = liftableEntitieList.remove("ALL_ANIMALS");
        EnumSet<EntityType> liftableEntities = EnumSet.noneOf(EntityType.class);
        liftableEntities.addAll(liftableEntitieList.stream()
                .map(key -> {
                    EntityType type = Registry.ENTITY_TYPE.get(NamespacedKey.minecraft(key.toLowerCase()));
                    return (type != null) ? type : warningInvalidEntityTypeName(key);
                })
                .filter(Objects::nonNull).toList()
        );
        this.liftableEntities = Collections.unmodifiableSet(liftableEntities);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (enableOnJoin && event.getPlayer().hasPermission("entityporter.use")) {
            porters.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        if (!player.getPassengers().isEmpty()) return;
        if (!player.hasPermission("entityporter.use")) return;
        if (!porters.contains(player.getUniqueId())) return;
        if (!enabledWorlds.contains(player.getWorld().getName().toLowerCase())) return;
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.PLAYER) return;
        if (liftableEntities.contains(entity.getType()) || (allAnimalsLiftable && entity instanceof Animals)) {
            Player porter = getEntityPorter(entity);
            if (porter == null) {
                Porter.of(player).lift(entity, createArmorStand);
                player.sendActionBar(local("lifted"));
                return;
            }
            if (allowLooting) {
                Porter.of(porter).drop();
                porter.sendActionBar(local("other-looted", player.getName()));
                Porter.of(player).lift(entity, createArmorStand);
                player.sendActionBar(local("looted", porter.getName()));
            } else {
                player.sendActionBar(local("has-porter", porter.getName()));
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (player.getPassengers().isEmpty()) return;
        Porter.of(player).drop();
        player.sendActionBar(local("dropped"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Porter.of(event.getPlayer()).drop();
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Porter.of(event.getPlayer()).drop();
    }

    @EventHandler public void onEntityDeath(EntityDeathEvent event) {onEntityDestroy(event);}
    @EventHandler public void onEntityRemove(EntityRemoveFromWorldEvent event) {onEntityDestroy(event);}
    private void onEntityDestroy(EntityEvent event) {
        if (liftableEntities.contains(event.getEntityType()) ||
                (allAnimalsLiftable && event.getEntityType().getEntityClass() != null &&
                        Animals.class.isAssignableFrom(event.getEntityType().getEntityClass()))) {
            Player porter = getEntityPorter(event.getEntity());
            if (porter != null) {
                Porter.of(porter).drop();
                porter.sendActionBar(local("lifting-entity-dead"));
            }
        }
    }

    private Player getEntityPorter(Entity entity) {
        Entity vehicle = entity.getVehicle();
        if (vehicle == null) return null;
        Player porter = null;
        if (vehicle instanceof Player player) {
            porter = player;
        } else if (vehicle instanceof ArmorStand && vehicle.getVehicle() instanceof Player player) {
            porter = player;
        }
        return porter;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(local("usage"));
            return true;
        }
        String subCommand = args[0].toLowerCase();
        if ("toggle".equals(subCommand)) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(local("player-command"));
                return true;
            }
            if (!player.hasPermission("petpassenger.use")) {
                player.sendMessage(local("no-permission"));
                return true;
            }
            UUID uuid = player.getUniqueId();
            if (!porters.contains(uuid)) {
                porters.add(uuid);
                player.sendMessage(local("enabled"));
            } else {
                porters.remove(uuid);
                player.sendMessage(local("disabled"));
            }
        } else if ("reload".equals(subCommand)) {
            if (!sender.hasPermission("petpassenger.reload")) {
                sender.sendMessage(local("no-permission"));
                return true;
            }
            onReload(false);
            sender.sendMessage(local("reloaded"));
        }
        return true;
    }

    @Override
    public @NonNull List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Stream.of("toggle", "reload")
                .filter(s -> sender.hasPermission("petpassenger."+("toggle".equals(s)?"use":s)) && s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        return List.of();
    }

    private Component local(String messageKey, Object... args) {
        String format = getConfig().getString("messages." + messageKey, messageKey);
        return MiniMessage.miniMessage().deserialize(MessageFormat.format(format, args));
    }

    private EntityType warningInvalidEntityTypeName(String typeName) {
        getLogger().warning("Invalid EntityType:" + typeName);
        return null;
    }

}
