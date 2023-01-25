package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

internal object MMVListener {
    @Ghost
    @SubscribeEvent
    fun onMythicMechanicLoad(event: MythicMechanicLoadEvent) {
        when (event.mechanicName.lowercase()) {
            in listOf("att-damage", "attdamage") -> {
                event.register(AttributeDamageV(event.config))
            }

            in listOf("att-cache", "attcache") -> {
                event.register(AttributeCacheV(event.config))
            }
        }
    }
}