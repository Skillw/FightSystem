package com.skillw.fightsystem.internal.core.fight

import com.skillw.asahi.api.member.context.AsahiContext
import com.skillw.fightsystem.FightSystem.debugLang
import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.mechanic.Mechanic
import com.skillw.pouvoir.api.plugin.map.component.Keyable
import org.bukkit.configuration.serialization.ConfigurationSerializable


/**
 * @className MechanicData
 *
 * @author Glom
 * @date 2022/8/21 10:29 Copyright 2022 user. All rights reserved.
 */
class MechanicData(
    override val key: Mechanic,
    val type: com.skillw.fightsystem.api.fight.DamageType,
    val context: AsahiContext = AsahiContext.create(),
) :
    Keyable<Mechanic>, AsahiContext by context, ConfigurationSerializable {
    private val mechanicKey = key.key
    fun run(fightData: FightData): Boolean {
        debugLang("fight-info-mechanic", mechanicKey)
        val result = key.run(fightData, this, type)
        if (!fightData.hasResult) return false
        debugLang("fight-info-mechanic-return", result.toString())
        result?.let { fightData[mechanicKey] = it }
        return true
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "mechanic" to mechanicKey,
            "context" to this
        )
    }

    override fun context(): AsahiContext {
        return this
    }

    override fun clone(): AsahiContext {
        return MechanicData(key, type, context.clone())
    }
}