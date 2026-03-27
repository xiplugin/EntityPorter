[**ENGLISH**](https://github.com/xiplugin/EntityPorter/blob/main/README.en.md)

### EntityPorter
**EntityPorter** 是一款为 **Paper/Spigot 1.21+** 设计的轻量化插件。它允许玩家将实体（如动物、村民）举在头顶进行运输。无论是搬运村民还是整理牧场，都不再需要复杂的绳索或小船！

### 功能特性
* **举起与运输:** **Shift + 右键** 举起实体实体（动物、村民等）
* **便捷放下:** 只需按下 **Shift** 即可放下实体。
* **抢夺系统:** 可配置是否允许从其他玩家头上“抢走”生物。
* **多世界管理:** 可限制插件仅在特定世界生效。
* **MiniMessage 支持:** 完美支持现代化的彩色文本格式。

### 配置说明 (`config.yml`)
```yaml
enable-on-join: true        # 玩家进入游戏时是否默认开启功能
allow-looting: true         # 是否允许从别人头上抢夺生物
create-armor-stand: true    # 是否在玩家和实体中间创建隐形盔甲架（建议开启）
enabled-worlds:             # 启用插件的世界
  - 'world'
  - 'test'

liftable-entities:          # 可被举起的实体类型（使用'ALL_ANIMALS'表示任何动物）
  - 'ALL_ANIMALS'
  - 'VILLAGER'

messages:
  usage: '<gray>错误的用法: 请使用<gold>/ep [reload|toggle]'
  player-command: '<gray>此命令只能由玩家执行'
  no-permission: '<red>你没有权限使用此命令'
  reloaded: '<gray>重载完成'
  enabled: '已开启举起实体功能'
  disabled: '已关闭举起实体功能'
  lifted: '已举起生物，按Shift放下'
  dropped: '已放下生物'
  lifting-entity-dead: '举着的生物死掉啦'
  other-looted: '举着的生物被{0}抢走啦'
  looted: '你抢走了{0}的生物'
  has-porter: '这个生物已经被{0}举起啦'
```

### 命令与权限
| 命令 | 别名 | 描述 | 默认权限 |
| :--- | :--- | :--- | :--- |
| `/entityporter toggle` | `/ep` | 开启/关闭举起功能 | `entityporter.use` (OP) |
| `/entityporter reload` | `/ep` | 重载配置文件 | `entityporter.reload` (OP) |

---