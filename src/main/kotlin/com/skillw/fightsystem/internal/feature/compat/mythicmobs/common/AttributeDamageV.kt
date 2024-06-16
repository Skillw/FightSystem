package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.util.syncTaskRun
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.mobs.GenericCaster
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.core.logging.MythicLogger
import io.lumine.mythic.core.skills.damage.DamagingMechanic
import io.lumine.mythic.core.skills.mechanics.CustomMechanic
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.platform.util.getMetaFirstOrNull

/**
 * @className AttributeDamageV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeDamageV(
    cm: CustomMechanic,
    mlc: MythicLineConfig
) : DamagingMechanic(cm.manager, cm.file, mlc.line, mlc), ITargetedEntitySkill {

    private val key: PlaceholderString = config.getPlaceholderString(arrayOf("key", "k"), "null")

    private val cache: PlaceholderString = config.getPlaceholderString(arrayOf("cache", "c"), "null")

    private val attacker: PlaceholderString = config.getPlaceholderString(arrayOf("attacker", "a"), "null")

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): SkillResult {
        val caster = data.caster.entity.bukkitEntity
        val target = targetAE.bukkitEntity
        val cacheKey = cache.get(data, targetAE)
        val attackerName = attacker.get(data, targetAE)
        val params = HashMap<String, Any>().apply {
            config.entrySet().forEach { (key, value) ->
                put(key, PlaceholderString.of(value).get(data, targetAE))
            }
        }
        val attacker =
            if (attackerName == "null") caster else Bukkit.getPlayerExact(attackerName)
                ?: return SkillResult.INVALID_TARGET
        val originCaster = data.caster
        data.caster = GenericCaster(BukkitAdapter.adapt(attacker))
        val cache = if (cacheKey == "null") null else attacker.getMetaFirstOrNull(cacheKey)
            ?.value() as? DataCache?
        if (attacker is LivingEntity && target is LivingEntity && !target.isDead) {
            val fightData = FightData(attacker, target) {
                cache?.let { cacheData ->
                    it.cache.setData(cacheData)
                }
                it["power"] = data.power.toDouble()
                it.putAll(params)
            }

            val damage = FightAPI.runFight(key.get(data, targetAE), fightData, damage = false)
            syncTaskRun {
                super.doDamage(data, targetAE, damage)
            }

            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeDamageMechanic fired for {0} with {1} power",
                damage, data.power
            )
        }
        data.caster = originCaster
        return SkillResult.SUCCESS
    }

}
