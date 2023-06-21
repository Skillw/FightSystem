package com.skillw.fightsystem.internal.feature.compat.skapi

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.pouvoir.util.isAlive
import com.sucy.skill.api.event.SkillDamageEvent
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

internal object SkillAPIListener {
    @Ghost
    @SubscribeEvent(EventPriority.LOWEST)
    fun e(event: SkillDamageEvent) {
        val attacker = event.damager
        val defender = event.target
        if (!attacker.isAlive() || !defender.isAlive()) {
            return
        }
        val originDamage = event.damage
        val triggerKey = "skill-api-${event.skill.key}-${event.classification}"
        if (!com.skillw.fightsystem.FightSystem.fightGroupManager.containsKey(triggerKey)) return
        val data = FightData(attacker, defender) {
            it["origin"] = originDamage
            it["event"] = event
        }
        val result = com.skillw.fightsystem.api.FightAPI.runFight(triggerKey, data)
        event.damage = if (result == -1.0) originDamage else result
    }
}
