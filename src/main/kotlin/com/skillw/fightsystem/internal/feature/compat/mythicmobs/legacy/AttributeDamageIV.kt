package com.skillw.fightsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.fightsystem.api.FightAPI.skipNextDamageCal
import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.pouvoir.util.decodeFromString
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.logging.MythicLogger
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.mechanics.DamageMechanic
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString
import org.bukkit.entity.LivingEntity

/**
 * @className AttributeDamageIV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeDamageIV(line: String?, private val mlc: MythicLineConfig) :
    DamageMechanic(line, mlc), ITargetedEntitySkill {
    val key: PlaceholderString = PlaceholderString.of(mlc.getString(arrayOf("key", "k"), "null"))
    val cache: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("cache", "c"), "null"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): Boolean {
        val caster = data.caster.entity.bukkitEntity
        val target = targetAE.bukkitEntity
        val cache = cache.get(data, targetAE)
        return if (caster is LivingEntity && target is LivingEntity && !target.isDead) {
            val cacheData = if (cache == "null") null else data.variables.getString(cache).decodeFromString<DataCache>()
            val fightData = FightData(caster, target) {
                cacheData?.let { cache -> it.cache = cache }
                it["power"] = data.power.toDouble()
                config.entrySet().forEach { entry ->
                    it[entry.key] = entry.value
                }
            }
            val damage = com.skillw.fightsystem.api.FightAPI.runFight(key.get(data, targetAE), fightData)
            skipNextDamageCal()
            doDamage(data.caster, targetAE, damage)
            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeDamageMechanic fired for {0} with {1} power",
                damage, data.power
            )
            true
        } else {
            false
        }
    }


}

