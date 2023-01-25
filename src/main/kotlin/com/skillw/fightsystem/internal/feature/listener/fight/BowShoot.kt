package com.skillw.fightsystem.internal.feature.listener.fight

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.FightData
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.event.SubscribeEvent

internal object BowShoot {
    @SubscribeEvent
    fun cacheData(event: EntityShootBowEvent) {
        val attacker = event.entity
        val cacheData = FightData(attacker, null)
        event.projectile.setMetadata(
            "ATTRIBUTE_SYSTEM_DATA",
            FixedMetadataValue(com.skillw.fightsystem.FightSystem.plugin, cacheData)
        )
    }
}