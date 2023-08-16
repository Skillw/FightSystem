package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.internal.feature.realizer.fight.AttackCooldownRealizer.CHARGE_KEY
import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.BaseRealizerManager
import com.skillw.pouvoir.api.feature.realizer.component.Awakeable
import com.skillw.pouvoir.api.feature.realizer.component.Switchable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.getMeta
import taboolib.platform.util.hasMeta
import taboolib.platform.util.setMeta

@AutoRegister
internal object ProjectileRealizer : BaseRealizer("projectile"), Awakeable, Switchable {

    override val file by lazy {
        FightSystem.options.file!!
    }

    override val manager: BaseRealizerManager
        get() = FightSystem.realizerManager

    override val defaultEnable: Boolean = true

    const val CACHE_KEY = "ATTRIBUTE_SYSTEM_DATA"

    @Ghost
    @SubscribeEvent
    fun projectileLaunch(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        val shooter = (projectile.shooter as? LivingEntity?) ?: return
        val cacheData = DataCache().attacker(shooter)
        projectile.cache(cacheData)
    }


    @Ghost
    @SubscribeEvent
    fun projectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity
        val hitEntity = (event.hitEntity as? LivingEntity?) ?: return
        val velocity = projectile.velocity.clone().subtract(hitEntity.velocity).length() / 2.0
        projectile.setMetadata(CHARGE_KEY, FixedMetadataValue(AttributeSystem.plugin, velocity))
    }

    fun Entity.cache(data: DataCache) {
        setMeta(CACHE_KEY, data)
    }

    fun Entity.cache(): DataCache? =
        getMeta(CACHE_KEY).firstOrNull()?.value() as? DataCache?

    fun Entity.charged(): Double? =
        if (hasMeta(CHARGE_KEY)) getMeta(CHARGE_KEY)[0].asDouble() else null
}