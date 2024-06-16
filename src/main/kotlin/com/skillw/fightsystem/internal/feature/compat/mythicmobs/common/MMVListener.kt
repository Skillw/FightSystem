package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.internal.manager.FSConfig
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.hasMeta

internal object MMVListener {
    @Awake(LifeCycle.ENABLE)
    fun ignore() {
        FightAPI.addIgnoreAttack { _, defender ->
            !FSConfig.mmDamageCal && defender.hasMeta("skill-damage")
        }
    }

    @Ghost
    @SubscribeEvent
    fun onMythicMechanicLoad(event: MythicMechanicLoadEvent) {
        when (event.mechanicName.lowercase()) {
            in listOf("att-damage", "attdamage") -> {
                event.register(AttributeDamageV(event.container, event.config))
            }

            in listOf("att-cache", "attcache") -> {
                event.register(AttributeCacheV(event.container, event.config))
            }
        }
    }
}