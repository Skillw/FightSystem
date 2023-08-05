package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.api.fight.FightData
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.core.logging.MythicLogger
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.util.sync

/**
 * @className AttributeDamageV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeDamageV(private val config: MythicLineConfig) :
    DamageMechanic(config) {
    val key: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("key", "k"), "null"))
    val cache: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("cache", "c"), "null"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): SkillResult {
        val caster = data.caster.entity.bukkitEntity
        val target = targetAE.bukkitEntity
        val cacheKey = cache.get(data, targetAE)
        val cache = if (cacheKey == "null") null else data.getMetadata(cacheKey)
            .run { if (isPresent) get() else null } as? DataCache?
        if (caster is LivingEntity && target is LivingEntity && !target.isDead) {
            val fightData = FightData(caster, target) {
                cache?.let { cacheData ->
                    it.cache.setData(cacheData)
                }
                it["power"] = data.power.toDouble()
                config.entrySet().forEach { entry ->
                    it[entry.key] = entry.value
                }
            }
            val damage = FightAPI.runFight(key.get(data, targetAE), fightData)
            if (!isPrimaryThread) sync { doDamage(data, targetAE, damage) }
            else doDamage(data, targetAE, damage)
            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeDamageMechanic fired for {0} with {1} power",
                damage, data.power
            )
        }
        return SkillResult.SUCCESS
    }


}

