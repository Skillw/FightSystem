package com.skillw.fightsystem.internal.core.asahi.infix

import com.skillw.fightsystem.api.FightAPI.isFighting
import com.skillw.pouvoir.internal.core.asahi.infix.bukkit.InfixLivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object ActionLivingEntity {
    @Awake(LifeCycle.ENABLE)
    fun action() {
        InfixLivingEntity.infix("in-fight") {
            it.isFighting()
        }
    }
}