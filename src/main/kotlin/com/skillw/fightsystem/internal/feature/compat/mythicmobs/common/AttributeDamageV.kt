package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.FightAPI.skipNextDamageCal
import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.pouvoir.util.decodeFromString
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.core.logging.MythicLogger
import org.bukkit.entity.LivingEntity

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
        val cache = cache.get(data, targetAE)
        if (caster is LivingEntity && target is LivingEntity && !target.isDead) {
            val cacheData = if (cache == "null") null else data.variables.getString(cache).decodeFromString<DataCache>()
            val fightData = FightData(caster, target) {
                cacheData?.let { cache -> it.cache = cache }
                it["power"] = data.power.toDouble()
                config.entrySet().forEach { entry ->
                    it[entry.key] = entry.value
                }
            }
            val damage = FightAPI.runFight(key.get(data, targetAE), fightData)
            skipNextDamageCal()
            doDamage(data, targetAE, damage)
            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeDamageMechanic fired for {0} with {1} power",
                damage, data.power
            )
        }
        return SkillResult.SUCCESS
    }


}

