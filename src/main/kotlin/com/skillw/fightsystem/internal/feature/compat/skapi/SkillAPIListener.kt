package com.skillw.fightsystem.internal.feature.compat.skapi

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.util.isAlive
import com.sucy.skill.api.event.SkillDamageEvent
import com.sucy.skill.api.skills.Skill
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

internal object SkillAPIListener {
    @Awake(LifeCycle.ENABLE)
    fun ignore() {
        FightAPI.addIgnoreAttack { _, _ ->
            !FSConfig.skapiDamageCal && FSConfig.skillAPI && Skill.isSkillDamage()
        }
    }

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
        if (!FightSystem.fightGroupManager.containsKey(triggerKey)) return
        val data = FightData(attacker, defender) {
            it["origin"] = originDamage
            it["event"] = event
        }
        val result = FightAPI.runFight(triggerKey, data, damage = false)
        event.damage = if (result == -1.0) originDamage else result
    }
}
