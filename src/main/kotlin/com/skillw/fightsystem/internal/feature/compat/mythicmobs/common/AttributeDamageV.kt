package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import com.skillw.fightsystem.api.FightAPI
import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.api.fight.FightData
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.mobs.GenericCaster
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.core.logging.MythicLogger
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.platform.util.getMetaFirstOrNull
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta

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
    val attacker: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("attacker", "a"), "null"))


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
        val cache = if (cacheKey == "null") null else (caster.getMetaFirstOrNull(cacheKey)?.value()
            ?: target.getMetaFirstOrNull(cacheKey)?.value()) as? DataCache?
        val attacker =
            if (attackerName == "null") caster else Bukkit.getPlayer(attackerName) ?: return SkillResult.INVALID_TARGET
        val originCaster = data.caster
        data.caster = GenericCaster(BukkitAdapter.adapt(attacker))

        if (attacker is LivingEntity && target is LivingEntity && !target.isDead) {
            val fightData = FightData(attacker, target) {
                cache?.let { cacheData ->
                    it.cache.setData(cacheData)
                }
                it[""]
                it["power"] = data.power.toDouble()
                it.putAll(params)
            }
            target.setMeta("skill-damage", true)
            val damage = FightAPI.runFight(key.get(data, targetAE), fightData, damage = false)
            target.removeMeta("skill-damage")
            submit { doDamage(data, targetAE, damage) }
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
