# FightSystem

插件永久免费

---

## 插件

| 说明   | 内容                                       |
|------|------------------------------------------|
| 兼容版本 | 1.12.2+                                  |
| 软依赖  | PlaceholderAPI Mythicmobs SkillAPI Magic |

## 介绍

**FightSystem** 是基于 **TabooLib VI** **Pouvoir** **AttributeSystem** 编写的一款战斗系统插件  
其基于**AttributeSystem**，提供了一些基本的属性 以及一套完整的,客制化的战斗流程系统  
您可以通过此插件编写复杂的战斗流程 \(例如原神的元素反应 英雄联盟的战斗系统 )  
其提供了包括但不限于以下功能/模块:

- [DamageTypeManager.kt](https://github.com/Glom-c/FightSystem/blob/master/src/main/kotlin/com/skillw/fightsystem/api/manager/DamageTypeManager.kt) ———
  伤害类型管理器
- [FightGroupManager.kt](https://github.com/Glom-c/FightSystem/blob/master/src/main/kotlin/com/skillw/fightsystem/api/manager/FightGroupManager.kt) ———
  战斗组管理器
- [FightStatusManager.kt](https://github.com/Glom-c/FightSystem/blob/master/src/main/kotlin/com/skillw/fightsystem/api/manager/FightStatusManager.kt) ———
  战斗状态管理器
- [MechanicManager.kt](https://github.com/Glom-c/FightSystem/blob/master/src/main/kotlin/com/skillw/fightsystem/api/manager/MechanicManager.kt) ———
  机制管理器
- [MessageBuilderManager.kt](https://github.com/Glom-c/FightSystem/blob/master/src/main/kotlin/com/skillw/fightsystem/api/manager/MessageBuilderManager.kt) ———
  信息交互管理器
- [PersonalManager.kt](https://github.com/Glom-c/FightSystem/blob/master/src/main/kotlin/com/skillw/fightsystem/api/manager/PersonalManager.kt) ———
  私人设置管理器

---

对于一些可扩展API，**FightSystem** 提供了脚本拓展
并使用脚本注解进行自动注册注销

详细请见WIKI

#### 机制 (Mechanic)

```kotlin
@AutoRegister
object MyMechanic : Mechanic("my_mechanic") {
    override fun exec(fightData: FightData, context: Map<String, Any>, damageType: DamageType): Any? {
        val attacker = fightData.attacker ?: return false
        val defender = fightData.defender
        val power = max(if (attacker is Player) attacker.level else 0, 0)
        val damage = Coerce.toDouble(context["formula"])
        val players = attacker.getNearbyEntities(10.0, 10.0, 10.0).filterIsInstance<Player>().map { adaptPlayer(it) }
        val location = adaptLocation(defender.location)
        ProxyParticle.EXPLOSION_LARGE.sendTo(location, range = 10.0)
        fightData.damageSources["my_mechanic_damage"] = Plus.element(power * 10 + damage)
        return true
    }
}
```

```javascript
var Coerce = static("Coerce");
var Player = find("org.bukkit.entity.Player");
var ProxyParticle = find(">taboolib.common.platform.ProxyParticle");

//@Mechanic(my_mechanic)
function exec(fightData, context, damageType) {
    var attacker = fightData.attacker;
    var defender = fightData.defender;
    var power = attacker instanceof Player ? attacker.level : 0;
    var damage = calculate(context.get("formula"), attacker);
    var location = defender.location;
    var particle = ProxyParticle.EXPLOSION_LARGE;
    Tool.sendSimpleParticle(particle, location, 36.0, 100, 1.0);
    fightData.damageSources.put(
        "my_mechanic_damage",
        Plus.element(power * 10 + damage)
    );
    return true;
}

```

## Links

WIKI [http://blog.skillw.com/#sort=FightSystem&doc=README.md](http://blog.skillw.com/#sort=fightsystem&doc=README.md)

JavaDoc [http://doc.skillw.com/FightSystem/](http://doc.skillw.com/fightsystem/)

[//]: # (MCBBS [https://www.mcbbs.net/thread-1221977-1-1.html]&#40;https://www.mcbbs.net/thread-1221977-1-1.html&#41;)

爱发电 [https://afdian.net/@glom\_](https://afdian.net/@glom_)
