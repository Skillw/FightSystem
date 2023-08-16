package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.component.Switchable
import com.skillw.pouvoir.api.feature.realizer.component.Valuable
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.FightAPI.intoFighting
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import taboolib.common.platform.event.SubscribeEvent

@AutoRegister
internal object FightStatusRealizer : BaseRealizer("fight-status"), Switchable, Valuable {

    override val file by lazy {
        FightSystem.options.file!!
    }
    override val defaultEnable: Boolean
        get() = true
    override val defaultValue: String
        get() = "100"

    @SubscribeEvent
    fun fighting(event: FightEvent.Pre) {
        if (isDisable()) return
        event.fightData.attacker?.intoFighting()
        event.fightData.defender?.intoFighting()
    }
}