package com.skillw.fightsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.fightsystem.api.fight.DataCache
import com.skillw.pouvoir.util.decodeFromString
import com.skillw.pouvoir.util.encodeJson
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.logging.MythicLogger
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.mechanics.DamageMechanic
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString
import io.lumine.xikage.mythicmobs.skills.variables.Variable
import io.lumine.xikage.mythicmobs.skills.variables.VariableType
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
    val expire: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("expire", "e"), "0"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): Boolean {
        val target = targetAE.bukkitEntity
        val key = key.get(data, targetAE)
        val type = type.get(data, targetAE)
        val expire = expire.get(data, targetAE).toLong()
        if (target is LivingEntity && !target.isDead) {
            when (type) {
                "attacker", "a", "attack" -> {
                    if (data.variables.has(key)) {
                        val cache = data.variables.getString(key).decodeFromString<DataCache>()
                        cache ?: data.variables.remove(key)
                        cache?.attacker(target)
                    }
                    if (!data.variables.has(key))
                        data.variables.put(
                            key,
                            Variable.ofType(VariableType.STRING, DataCache().attacker(target).encodeJson(), expire)
                        )
                }

                "defender", "d", "defend" -> {
                    if (data.variables.has(key)) {
                        val cache = data.variables.getString(key).decodeFromString<DataCache>()
                        cache ?: data.variables.remove(key)
                        cache?.defender(target)
                    }
                    if (!data.variables.has(key))
                        data.variables.put(
                            key,
                            Variable.ofType(VariableType.STRING, DataCache().defender(target).encodeJson(), expire)
                        )
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

