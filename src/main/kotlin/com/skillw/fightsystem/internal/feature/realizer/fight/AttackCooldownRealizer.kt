package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.Awakeable
import com.skillw.attsystem.api.realizer.component.Switchable
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.fightsystem.internal.feature.realizer.fight.ProjectileRealizer.charged
import com.skillw.fightsystem.util.syncTaskRun
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.plugin.map.BaseMap
import com.skillw.pouvoir.util.attribute.BukkitAttribute
import com.skillw.pouvoir.util.attribute.getAttribute
import com.skillw.pouvoir.util.put
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.cbool
import taboolib.common5.cdouble
import taboolib.library.xseries.XMaterial
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt


@AutoRegister
internal object AttackCooldownRealizer : BaseRealizer("attack-cooldown"), Switchable, Awakeable {

    override val file by lazy {
        FightSystem.options.file!!
    }
    override val defaultEnable: Boolean
        get() = true

    private val enableCooldown: Boolean
        get() = config.getOrDefault("type", "cooldown").toString().lowercase() == "cooldown"

    private val attackAnyTime: Boolean
        get() = config["damage-any-time"].cbool
    private val damageCharged
        get() = config["charged"].cbool
    internal val chargeBasedCooldown: Boolean
        get() = config.getOrDefault("charge-based", "cooldown").toString().lowercase() == "cooldown"
    private val minCharge
        get() = config.getOrDefault("min-charge", 0.05).cdouble

    private val disableTypes = HashSet<Material>()

    const val CHARGE_KEY = "ATTRIBUTE_SYSTEM_FORCE"

    @SubscribeEvent
    fun damageCharged(event: FightEvent.Pre) {
        if (event.key != "attack-damage") return
        val attacker = event.fightData.attacker as? Player? ?: return
        val defender = event.fightData.defender ?: return

        if (FightAPI.filters.any { it(attacker, defender) }) return


        val main = attacker.inventory.itemInMainHand
        val isCooldown = attacker.inCooldown(main)

        FightSystem.debug("Attack Charge & Cooldown Checking Handling!")
        //在冷却 && 不能随时攻击 就取消
        if (!attackAnyTime && isCooldown) {
            FightSystem.debug("Cancelled because can't attack while cooldown time")
            event.isCancelled = true
            return
        }

        val originEvent = event.fightData["event"] as? EntityDamageByEntityEvent ?: return
        //计算蓄力程度
        //  这个函数是获取 弓/弩 的蓄力程度，若返回null则代表不是抛射物攻击，进而进行近战时的蓄力计算
        val charge = originEvent.damager.charged() ?: attacker.damageCharged(main, originEvent) ?: 1.0
        event.fightData.let {
            it["force"] = charge
            it["charge"] = charge
        }
        event.isCancelled = (charge < minCharge).also {
            if (it)
                FightSystem.debug("Cancelled because charge value < minCharge $minCharge")
        }
    }

    @SubscribeEvent(EventPriority.HIGHEST)
    fun cooldown(event: FightEvent.Post) {
        if (!enableCooldown) return
        if (event.key != "attack-damage") return
        if (event.fightData["projectile"] == true) return
        val attacker = event.fightData.attacker as? Player? ?: return
        val defender = event.fightData.defender ?: return
        if (FightAPI.filters.any { it(attacker, defender) }) return
        val material = attacker.inventory.itemInMainHand.type
        if (disableTypes.contains(material)) return
        FightSystem.debug("Attack Cooldown Handling!")
        attacker.cooldown(material, attacker.getAttribute(BukkitAttribute.ATTACK_SPEED)?.value ?: return)
    }

    private fun Player.damageCharged(main: ItemStack, event: EntityDamageByEntityEvent): Double? {
        if (!damageCharged) return null
        return if (chargeBasedCooldown)
            pullProcess(main.type).also {
                cooldown(main.type, getAttribute(BukkitAttribute.ATTACK_SPEED)?.value ?: return@also)
            }
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
        for (material in config.getOrDefault(
            "options.fight.attack-cooldown.no-cooldown-types",
            emptyList<String>()
        ) as List<String>) {
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

    internal fun Player.pullProcess(material: Material): Double {
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
        syncTaskRun {
            setCooldown(material, (seconds * 20).roundToInt())
        }
    }

}