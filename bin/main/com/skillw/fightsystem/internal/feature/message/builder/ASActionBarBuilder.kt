package com.skillw.fightsystem.internal.feature.message.builder

import com.skillw.fightsystem.FightSystem.message
import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.message.Message
import com.skillw.fightsystem.api.fight.message.MessageBuilder
import com.skillw.fightsystem.internal.feature.message.ASActionBar
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister

@AutoRegister
object ASActionBarBuilder : MessageBuilder {

    override val key: String = "action_bar"

    override fun build(
        damageType: com.skillw.fightsystem.api.fight.DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message {
        val typeStr = type.name.lowercase()
        val typeText =
            fightData.handleStr(damageType["$typeStr-action-bar"].toString().replace("{name}", damageType.name))
        val text =
            if (first) fightData.handleStr(
                message.getString("fight-message.action-bar.$typeStr.text")
                    ?.replace("{message}", typeText)
                    ?: typeText
            )
            else typeText

        return ASActionBar(StringBuilder(text), fightData)
    }
}
