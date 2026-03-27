[**简体中文**](https://github.com/xiplugin/EntityPorter/blob/main/README.md)

### EntityPorter
**EntityPorter** is a lightweight Minecraft server plugin designed for **Paper/Spigot 1.21+**. It allows players to pick up, carry, and transport entities (like Animals and Villagers) on their heads. Perfect for moving villagers or organizing your farm without the hassle of lead ropes or boats!

### ✨ Features
* **Pick & Carry:** **Shift + Right click** to lift entities (Animals, Villagers, etc.).
* **Simple Drop:** Just press **Shift** to release the entity.
* **Looting System:** Configurable option to allow "stealing" an entity from another player's head.
* **World Management:** Restrict the plugin to specific worlds.
* **MiniMessage Support:** Full support for modern, colorful hex messages.

### 🛠️ Configuration (`config.yml`)
```yaml
enable-on-join: true        # Enable lifting functionality when player joins
allow-looting: true         # Allow players to loot entities from others
create-armor-stand: true    # Create an invisible armor stand between the player and the entity (recommended)
enabled-worlds:             # Worlds where the plugin is active
  - 'world'
  - 'test'

liftable-entities:          # Types of entities that can be lifted (can use 'ALL_ANIMALS')
  - 'ALL_ANIMALS'
  - 'VILLAGER'

messages:
  usage: '<gray>Usage: <gold>/ep [reload|toggle]'
  player-command: '<gray>This command is for players only'
  no-permission: '<red>You do not have permission'
  reloaded: '<gray>Reloaded successfully'
  enabled: 'Lifting enabled'
  disabled: 'Lifting disabled'
  lifted: 'Entity lifted! Press Shift to drop'
  dropped: 'Entity dropped'
  lifting-entity-dead: 'The entity died'
  other-looted: 'Your entity was taken by {0}'
  looted: 'You took {0}''s entity'
  has-porter: 'This entity is already carried by {0}'
```

### 💻 Commands & Permissions
| Command | Alias | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/entityporter toggle` | `/ep` | Toggle lifting on/off | `entityporter.use` (OP) |
| `/entityporter reload` | `/ep` | Reload config | `entityporter.reload` (OP) |

---