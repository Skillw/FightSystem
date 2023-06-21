package com.skillw.fightsystem.api.event

import com.skillw.fightsystem.api.fight.FightData
import com.skillw.pouvoir.api.feature.trigger.BukkitTrigger
import taboolib.platform.type.BukkitProxyEvent

class FightEvent {
    /**
     * 攻击前事件 机制组还没有开始运行
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */

    @BukkitTrigger(name = "before entity fight")
    class Pre(val key: String, val fightData: FightData) : BukkitProxyEvent() {
        override val allowCancelled: Boolean = true
        val attacker = fightData.attacker
        val defender = fightData.defender
        val hasAttacker = attacker != null
        val hasDefender = defender != null
    }

    /**
     * 攻击中事件 机制组运行完毕，未计算出总伤害
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */
    @BukkitTrigger(name = "during entity fight")
    class Process(val key: String, val fightData: FightData) : BukkitProxyEvent() {
        override val allowCancelled: Boolean = true
        val defender = fightData.defender
        val attacker = fightData.attacker
        val hasAttacker = attacker != null
        val hasDefender = defender != null
    }

    /**
     * 攻击后事件 机制组运行完毕，已计算出伤害
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */
    @BukkitTrigger(name = "after entity fight")
    class Post(val key: String, val fightData: FightData) : BukkitProxyEvent() {
        val defender = fightData.defender
        val attacker = fightData.attacker
        val hasAttacker = attacker != null
        val hasDefender = defender != null
    }
}
