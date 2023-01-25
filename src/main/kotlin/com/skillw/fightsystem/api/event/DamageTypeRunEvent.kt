package com.skillw.fightsystem.api.event

import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.pouvoir.api.feature.trigger.BukkitTrigger
import taboolib.platform.type.BukkitProxyEvent


class DamageTypeRunEvent {
    /**
     * 运行此伤害类型下的判断enable以及机制前
     *
     * @property type 伤害类型
     * @property fightData 战斗数据
     * @property enable 是否启用
     */
    @BukkitTrigger("before damage type run")
    class Pre(
        val type: DamageType,
        val fightData: FightData,
        var enable: Boolean,
    ) : BukkitProxyEvent() {
        override val allowCancelled = true
    }

    /**
     * 运行此伤害类型下的判断enable以及机制后
     *
     * @property type 伤害类型
     * @property fightData 战斗数据
     */
    @BukkitTrigger("after damage type run")
    class Post(
        val type: DamageType,
        val fightData: FightData,
    ) : BukkitProxyEvent() {
        override val allowCancelled = true
    }

}
