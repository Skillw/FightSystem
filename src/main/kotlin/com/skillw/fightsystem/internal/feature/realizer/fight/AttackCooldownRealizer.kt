package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Awakeable
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.util.AttributeUtils.getAttribute
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.plugin.map.BaseMap
import com.skillw.pouvoir.util.put
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt


private const val CHARGE_KEY = "ATTRIBUTE_SYSTEM_FORCE"

@AutoRegister
internal object AttackCooldownRealizer : BaseRealizer("attack-cooldown"), Switchable, Awakeable {

    override val fileName: String = "options.yml"
    override val defaultEnable: Boolean
        get() = true

    private val enableCooldown: Boolean
        get() = config.get("type", "cooldown").lowercase() == "cooldown"

    private val attackAnyTime: Boolean
        get() = config.getBoolean("damage-any-time")
    private val damageCharged
        get() = config.getBoolean("damage-charged")
    private val chargeBasedCooldown: Boolean
        get() = config.getString("charge-based").lowercase() == "cooldown"
    private val minCharge
        get() = config.getDouble("min-charge")

    private val disableTypes = HashSet<Material>()

    @SubscribeEvent
    fun damageCharged(event: FightEvent.Pre) {
        if (event.key != "attack-damage") return
        val attacker = event.fightData.attacker as? Player? ?: return

        val main = attacker.inventory.itemInMainHand
        val isCooldown = attacker.inCooldown(main)

        //在冷却 && 不能随时攻击 就取消
        if (!attackAnyTime && isCooldown) {
            event.isCancelled = true
            return
        }

        val originEvent = event.fightData["event"] as EntityDamageByEntityEvent
        //计算蓄力程度
        //  这个函数是获取 弓/弩 的蓄力程度，若返回null则代表不是抛射物攻击，进而进行近战时的蓄力计算
        val charge = originEvent.damager.projectileCharged() ?: attacker.damageCharged(main, originEvent) ?: 1.0
        event.fightData.let {
            it["force"] = charge
            it["charge"] = charge
        }
        event.isCancelled = charge < minCharge
    }

    @SubscribeEvent
    fun cooldown(event: FightEvent.Post) {
        if (!enableCooldown) return
        if (event.key != "attack-damage") return
        if (event.fightData["projectile"] == true) return
        val attacker = event.fightData.attacker as? Player? ?: return
        val material = attacker.inventory.itemInMainHand.type
        if (disableTypes.contains(material)) return
        attacker.cooldown(material, attacker.getAttribute(BukkitAttribute.ATTACK_SPEED)?.value ?: return)
    }

    @SubscribeEvent
    fun chargeAddition(event: EntityShootBowEvent) {
        event.projectile.setMetadata(CHARGE_KEY, FixedMetadataValue(AttributeSystem.plugin, event.force))
    }

    private fun Player.damageCharged(main: ItemStack, event: EntityDamageByEntityEvent): Double? {
        if (!damageCharged) return null
        return if (chargeBasedCooldown)
            pullProcess(main.type)
        else
            getAttribute(BukkitAttribute.ATTACK_DAMAGE)?.value?.let { damage ->
                event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE) / damage
            }
    }

    init {
        defaultConfig.putAll(
            linkedMapOf(
                "type" to "cooldown",
                "damage-any-time" to true,
                "damage-charged" to true,
                "charge-based" to "vanilla",
                "min-charge" to 0.05,
                "no-cooldown-types" to listOf("BOW", "CROSSBOW")
            )
        )
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        disableTypes.clear()
        for (material in config.get("options.fight.attack-cooldown.no-cooldown-types", emptyList<String>())) {
            val xMaterial = XMaterial.matchXMaterial(material)
            if (xMaterial.isPresent) {
                disableTypes.add(xMaterial.get().parseMaterial() ?: continue)
            } else {
                val materialMC = Material.matchMaterial(material) ?: continue
                disableTypes.add(materialMC)
            }
        }
    }

    private val cooldownData = BaseMap<UUID, BaseMap<Material, CooldownTime>>()

    /**
     * Cooldown time
     *
     * @constructor Create empty Cooldown time
     * @property total 冷却时间 秒
     */
    class CooldownTime(time: Double) {
        val total = time * 1000

        /** Start */
        val start = System.currentTimeMillis()
    }

    private fun Player.pullProcess(material: Material): Double {
        val key = uniqueId
        if (!cooldownData.containsKey(key) || !cooldownData[key]!!.containsKey(material)) return 1.0
        val (total, start) = cooldownData[key]?.get(material)?.run { total to start } ?: return 1.0
        val now = System.currentTimeMillis()
        val value = min((now - start) / total, 1.0)
        cooldownData[key]?.remove(material)
        return value
    }

    private fun Player.inCooldown(itemStack: ItemStack): Boolean {
        return getCooldown(itemStack.type) > 0
    }

    private fun Player.cooldown(material: Material, attackSpeed: Double) {
        if (attackSpeed <= 0.0) return
        val seconds = 1 / attackSpeed
        cooldownData.put(uniqueId, material, CooldownTime(seconds))
        setCooldown(material, (seconds * 20).roundToInt())
    }

}

fun Entity.projectileCharged(): Double? =
    if (hasMetadata(CHARGE_KEY)) getMetadata(CHARGE_KEY)[0].asDouble() else null