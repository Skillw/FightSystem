package com.skillw.fightsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.fightsystem.api.fight.FightData
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.logging.MythicLogger
import io.lumine.xikage.mythicmobs.mobs.GenericCaster
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.mechanics.DamageMechanic
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.platform.util.getMetaFirstOrNull
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta

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
    val attacker: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("attacker", "a"), "null"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): Boolean {
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
            if (attackerName == "null") caster else Bukkit.getPlayer(attackerName) ?: return false
        val origin = data.caster
        data.caster = GenericCaster(BukkitAdapter.adapt(attacker))
        val cache = if (cacheKey == "null") null else attacker.getMetaFirstOrNull(cacheKey)
            ?.value() as? DataCache?
        return if (attacker is LivingEntity && target is LivingEntity && !target.isDead) {
            val fightData = FightData(attacker, target) {
                cache?.let { cacheData ->
                    it.cache.setData(cacheData)
                }
                it["power"] = data.power.toDouble()
                it.putAll(params)
            }
            target.setMeta("skill-damage", true)
            val damage =
                com.skillw.fightsystem.api.FightAPI.runFight(key.get(data, targetAE), fightData, damage = false)
            target.removeMeta("skill-damage")
            submit { doDamage(data.caster, targetAE, damage) }
            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeDamageMechanic fired for {0} with {1} power",
                damage, data.power
            )
            true
        } else {
            false
        }.also {
            data.caster = origin
        }
    }


}

