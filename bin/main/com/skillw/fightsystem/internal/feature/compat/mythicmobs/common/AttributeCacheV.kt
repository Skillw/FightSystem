package com.skillw.fightsystem.internal.feature.compat.mythicmobs.common

import com.skillw.fightsystem.api.fight.DataCache
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.core.logging.MythicLogger
import org.bukkit.entity.LivingEntity

/**
 * @className AttributeCacheV
 *
 * @author Glom
 * @date 2023年1月21日 8:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeCacheV(config: MythicLineConfig) :
    ITargetedEntitySkill {
    val key: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("key", "k"), "null"))
    val type: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("type", "t"), "attacker"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): SkillResult {
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
        }
        return SkillResult.SUCCESS
    }


}

