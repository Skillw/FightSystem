package com.skillw.fightsystem.internal.feature.message.builder

import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.message.Message
import com.skillw.fightsystem.api.fight.message.MessageBuilder
import com.skillw.fightsystem.internal.feature.message.ASHologramGroup
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister

@AutoRegister
object ASHoloBuilder : MessageBuilder {

    override val key: String = "holo"


    override fun build(
        damageType: com.skillw.fightsystem.api.fight.DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message {
        val typeStr = type.name.lowercase()
        val text = fightData.handleStr(damageType["$typeStr-holo"].toString(), false).replace("{name}", damageType.name)
        return ASHologramGroup(mutableListOf(text), fightData.defender!!.eyeLocation, "fight-message.holo", fightData)
    }
}
