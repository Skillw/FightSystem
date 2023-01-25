package com.skillw.fightsystem.internal.feature.compat.mythicmobs.legacy

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

internal object MMIVListener {
    @Ghost
    @SubscribeEvent
    fun onMythicMechanicLoad(event: MythicMechanicLoadEvent) {
        when (event.mechanicName.lowercase()) {
            in listOf("att-damage", "attdamage") -> {
                event.register(AttributeDamageIV(event.config.line, event.config))
            }

            in listOf("att-cache", "attcache") -> {
                event.register(AttributeCacheIV(event.config.line, event.config))
            }
        }
    }

}