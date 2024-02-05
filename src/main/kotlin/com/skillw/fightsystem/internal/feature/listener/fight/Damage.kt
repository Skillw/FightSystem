package com.skillw.fightsystem.internal.feature.listener.fight

import com.skillw.fightsystem.api.FightAPI.intoFighting
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.util.isAlive
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker

internal object Damage {
    @SubscribeEvent(EventPriority.HIGHEST)
    fun damageEntity(event: EntityDamageEvent) {
        val attacker: LivingEntity? = if (event is EntityDamageByEntityEvent) event.attacker else null
        val defender = event.entity as? LivingEntity? ?: return
        if (!defender.isAlive()) return
        val cause = event.cause.name.lowercase()
        val key = "damage-cause-$cause"
        if (!com.skillw.fightsystem.FightSystem.fightGroupManager.containsKey(key)) return
        val data = FightData(attacker, defender) {
            it["origin"] = event.damage; it["event"] = event
        }
        val result = com.skillw.fightsystem.api.FightAPI.runFight(key, data, damage = false)
        if (result > 0.0) {
            if (!FSConfig.isVanillaArmor && attacker?.type != EntityType.ARMOR_STAND) {
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
            }
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, result)
            attacker?.intoFighting()
            defender.intoFighting()
        } else if (result < 0.0) {
            event.isCancelled = true
        }
    }
}