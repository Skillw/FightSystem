package com.skillw.fightsystem.api.fight.mechanic

import com.skillw.fightsystem.api.fight.FightData
import com.skillw.pouvoir.api.plugin.map.component.Registrable


/**
 * Mechanic
 *
 * @constructor Create empty Mechanic
 * @property key 键
 */
abstract class Mechanic(override val key: String) :
    Registrable<String> {

    /**
     * Exec
     *
     * @param fightData 战斗数据
     * @param context 上下文 （机制在战斗组中的参数）
     * @param damageType 伤害类型
     * @return 返回值
     */
    abstract fun exec(
        fightData: FightData,
        context: Map<String, Any>,
        damageType: com.skillw.fightsystem.api.fight.DamageType,
    ): Any?

    /** 是否在重载时删除 */
    var release = false

    /**
     * Run
     *
     * 运行机制
     *
     * @param fightData 战斗数据
     * @param context 上下文 （机制在战斗组中的参数）
     * @param damageType 伤害类型
     * @return 返回值
     */
    fun run(
        fightData: FightData,
        context: Map<String, Any>,
        damageType: com.skillw.fightsystem.api.fight.DamageType,
    ): Any? {
        val before =
            com.skillw.fightsystem.api.event.MechanicRunEvent.Pre(this, fightData, context, damageType, null)
        if (before.isCancelled) return null
        val result = exec(fightData, context, damageType)
        val post =
            com.skillw.fightsystem.api.event.MechanicRunEvent.Post(this, fightData, context, damageType, result)
        post.call()
        if (post.isCancelled) return null
        return post.result
    }

    final override fun register() {
        com.skillw.fightsystem.FightSystem.mechanicManager.register(this)
    }

}
