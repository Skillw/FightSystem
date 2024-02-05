package com.skillw.fightsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.attsystem.api.AttrAPI.getAttrData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.fightsystem.internal.manager.FSConfig
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Damageable
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.cbool
import taboolib.platform.util.*

internal object MMIVListener {

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
                event.register(AttributeDamageIV(event.config.line, event.config))
            }

            in listOf("att-cache", "attcache") -> {
                event.register(AttributeCacheIV(event.config.line, event.config))
            }
        }
    }

}