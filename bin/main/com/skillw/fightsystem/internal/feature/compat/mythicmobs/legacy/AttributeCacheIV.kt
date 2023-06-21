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

/**
 * @className AttributeDamageIV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeCacheIV(line: String?, private val mlc: MythicLineConfig) :
    DamageMechanic(line, mlc), ITargetedEntitySkill {
    val key: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("key", "k"), "null"))
    val type: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("type", "t"), "attacker"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): Boolean {
        val target = targetAE.bukkitEntity
        val key = key.get(data, targetAE)
        val type = type.get(data, targetAE)
        if (target is LivingEntity && !target.isDead) {
            when (type) {
                "attacker", "a", "attack" -> {
                    if (data.getMetadata(key).isPresent) {
                        val cache = data.getMetadata(key).get() as DataCache
                        data.setMetadata(key, cache.attacker(target))
                    } else
                        data.setMetadata(key, DataCache().attacker(target))

                }

                "defender", "d", "defend" -> {
                    if (data.getMetadata(key).isPresent) {
                        val cache = data.getMetadata(key).get() as DataCache
                        data.setMetadata(key, cache.defender(target))
                    } else
                        data.setMetadata(key, DataCache().defender(target))
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

