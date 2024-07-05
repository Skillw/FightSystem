package com.skillw.fightsystem.api.fight.message

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.pouvoir.api.plugin.map.component.Registrable

/**
 * @className MessageBuilder
 *
 * @author Glom
 * @date 2022/8/1 4:32 Copyright 2022 user. All rights reserved.
 */
interface MessageBuilder : Registrable<String> {
    fun build(
        damageType: DamageType,
        fightData: FightData,
        first: Boolean,
        type: Message.Type,
    ): Message

    override fun register() {
        FightSystem.messageBuilderManager.attack.register(this)
        FightSystem.messageBuilderManager.defend.register(this)
    }
}