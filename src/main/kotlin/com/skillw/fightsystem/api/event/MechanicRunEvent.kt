package com.skillw.fightsystem.api.event

import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.mechanic.Mechanic
import taboolib.platform.type.BukkitProxyEvent


class MechanicRunEvent {
    /**
     * 运行机制前
     *
     * @property mechanic 机制
     * @property fightData 战斗数据
     * @property context 上下文(战斗组中这个机制的参数)
     * @property damageType 伤害类型
     * @property result 机制运行结果
     */
    class Pre(
        val mechanic: Mechanic,
        val fightData: FightData,
        val context: Map<String, Any>,
        val damageType: com.skillw.fightsystem.api.fight.DamageType,
        var result: Any?,
    ) : BukkitProxyEvent() {
        override val allowCancelled = true
    }

    /**
     * 运行机制后
     *
     * @property mechanic 机制
     * @property fightData 战斗数据
     * @property context 上下文(战斗组中这个机制的参数)
     * @property damageType 伤害类型
     * @property result 机制运行结果
     */

    class Post(
        val mechanic: Mechanic,
        val fightData: FightData,
        val context: Map<String, Any>,
        val damageType: com.skillw.fightsystem.api.fight.DamageType,
        var result: Any?,
    ) : BukkitProxyEvent() {
        override val allowCancelled = true
    }

}
