package com.skillw.fightsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.fightsystem.api.fight.DataCache
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.logging.MythicLogger
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.mechanics.DamageMechanic
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString
import org.bukkit.entity.LivingEntity
import taboolib.platform.util.getMeta
import taboolib.platform.util.setMeta

/**
 * @className AttributeDamageIV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeCacheIV(line: String?, mlc: MythicLineConfig) :
    DamageMechanic(line, mlc), ITargetedEntitySkill {
    val key: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("key", "k"), "null"))
    val type: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("type", "t"), "attacker"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): Boolean {
        val target = targetAE.bukkitEntity
        val key = key.get(data, targetAE)
        val type = type.get(data, targetAE)

        val params = HashMap<String, Any>().apply {
            config.entrySet().forEach { (key, value) ->
                if (key == "key" || key == "type") return@forEach
                put(key, PlaceholderString.of(value).get(data, targetAE))
            }
        }

        if (target is LivingEntity && !target.isDead) {
            when (type) {
                "attacker", "a", "attack" -> {
                    val meta = target.getMeta(key)
                    if (meta.isNotEmpty()) {
                        val cache = meta.first().value() as DataCache
                        target.setMeta(key, cache.attacker(target).variables(params))
                    } else
                        target.setMeta(key, DataCache().attacker(target).variables(params))
                }

                "defender", "d", "defend" -> {
                    val meta = target.getMeta(key)
                    if (meta.isNotEmpty()) {
                        val cache = meta.first().value() as DataCache
                        target.setMeta(key, cache.defender(target).variables(params))
                    } else
                        target.setMeta(key, DataCache().defender(target).variables(params))
                }
            }

            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeCache fired for {0} with key {1}",
                target, key
            )
            return true
        }
        return false
    }


}

