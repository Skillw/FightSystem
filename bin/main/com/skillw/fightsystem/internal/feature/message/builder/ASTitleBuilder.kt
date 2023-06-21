package com.skillw.fightsystem.internal.feature.message.builder

import com.skillw.fightsystem.FightSystem.message
import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.message.Message
import com.skillw.fightsystem.api.fight.message.MessageBuilder
import com.skillw.fightsystem.internal.feature.message.ASTitle
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister

@AutoRegister
object ASTitleBuilder : MessageBuilder {

    override val key: String = "title"


    override fun build(
        damageType: com.skillw.fightsystem.api.fight.DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message {
        val typeStr = type.name.lowercase()
        val title = fightData.handleStr(damageType["$typeStr-title"].toString().replace("{name}", damageType.name))
        val subTitle =
            fightData.handleStr(damageType["$typeStr-sub-title"].toString().replace("{name}", damageType.name))
        val titleStr =
            if (first) fightData.handleStr(
                message.getString("fight-message.title.$typeStr.title")?.replace("{message}", title)
                    ?: title
            )
            else title

        val subTitleStr =
            if (first) fightData.handleStr(
                message.getString("fight-message.title.$typeStr.sub-title")
                    ?.replace("{message}", subTitle)
                    ?: subTitle
            )
            else subTitle

        return ASTitle(StringBuilder(titleStr), StringBuilder(subTitleStr), fightData)
    }
}
