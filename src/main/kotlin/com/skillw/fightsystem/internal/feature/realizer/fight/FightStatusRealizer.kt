package com.skillw.fightsystem.internal.feature.realizer.fight

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.api.realizer.component.sub.Valuable
import com.skillw.fightsystem.api.FightAPI.intoFighting
import com.skillw.fightsystem.api.event.FightEvent
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import taboolib.common.platform.event.SubscribeEvent

@AutoRegister
internal object FightStatusRealizer : BaseRealizer("fight-status"), Switchable, Valuable {

    override val fileName: String = "options.yml"
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