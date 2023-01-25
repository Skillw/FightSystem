package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.api.realizer.component.sub.Valuable
import com.skillw.attsystem.api.realizer.component.sub.Vanillable
import com.skillw.attsystem.util.AntiCheatUtils
import com.skillw.attsystem.util.AttributeUtils.getAttribute
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.util.getEntityRayHit
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.sendTo
import taboolib.common.util.Location
import taboolib.common.util.sync
import taboolib.library.xseries.XSound

@AutoRegister
internal object AttackDistanceRealizer : BaseRealizer("attack-distance"), Switchable, Vanillable, Valuable {

    override val fileName: String = "options.yml"
    override val defaultEnable: Boolean
        get() = true
    override val defaultValue: String
        get() = "0"
    override val defaultVanilla: Boolean
        get() = true

    private val isDistanceEffect
        get() = config.get("distance-attack.effect", true)
    private val isDistanceSound
        get() = config.get("distance-attack.sound", true)

    private val defaultDistance: Double
        get() = config.get("vanilla-distance.default", 3.0)
    private val creativeDistance: Double
        get() = config.get("vanilla-distance.creative", 4.5)


    @SubscribeEvent
    fun distanceAttack(event: PlayerInteractEvent) {
        val player = event.player
        if (event.action != Action.LEFT_CLICK_AIR || player.gameMode == GameMode.SPECTATOR) return
        if (Pouvoir.sync)
            distanceAttack(player)
        else submitAsync { distanceAttack(player) }
    }

    @SubscribeEvent
    fun cancelIfTooDistant(event: FightEvent.Pre) {
        if (event.key != "attack-damage") return
        if (event.fightData["projectile"] == false) return
        val attacker = event.fightData.attacker as? Player? ?: return
        val defender = event.fightData.defender ?: return
        //不满足攻击距离，则取消伤害并跳过计算
        if (defender.location.distance(attacker.eyeLocation) > AttackDistanceRealizer.value(attacker)) {
            event.isCancelled = true
            return
        }
    }

    init {
        defaultConfig.putAll(
            linkedMapOf(
                "vanilla-distance" to linkedMapOf(
                    "default" to 3,
                    "creative" to 4.5
                ),
                "distance-attack" to linkedMapOf(
                    "effect" to true,
                    "sound" to true
                )
            )
        )
    }

    private fun distanceDamage(player: Player, entity: LivingEntity) {
        AntiCheatUtils.bypassAntiCheat(player)
        val attackDamage = player.getAttribute(BukkitAttribute.ATTACK_DAMAGE)?.value ?: 0.0
        if (isDistanceSound) XSound.ENTITY_PLAYER_ATTACK_SWEEP.play(entity, 1.0f, 1.0f)
        if (isDistanceEffect) {
            val location = entity.eyeLocation
            ProxyParticle.SWEEP_ATTACK.sendTo(Location(player.world.name, location.x, location.y, location.z))
        }
        entity.damage(attackDamage.coerceAtLeast(1.0), player)
        AntiCheatUtils.recoverAntiCheat(player)
    }

    private val Player.defaultAttackDistance: Double
        get() = if (!isEnableVanilla()) 0.0 else if (gameMode == GameMode.CREATIVE) creativeDistance else defaultDistance

    private fun distanceAttack(player: Player) {
        val attackDistance = AttackDistanceRealizer.value(player)
        val entity = player.getEntityRayHit(attackDistance) as? LivingEntity? ?: return
        if (entity.location.distance(player.eyeLocation) <= player.defaultAttackDistance) return
        if (isPrimaryThread) distanceDamage(player, entity) else sync { distanceDamage(player, entity) }
    }

}