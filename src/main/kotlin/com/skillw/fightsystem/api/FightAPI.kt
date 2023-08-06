package com.skillw.fightsystem.api

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.FightSystem.fightGroupManager
import com.skillw.fightsystem.api.fight.FightData
import org.bukkit.entity.LivingEntity

/**
 * Fight API
 *
 * 提供了一些拓展函数，用于快速调用API
 *
 * @constructor Create empty Fight a p i
 */

object FightAPI {

    internal val filters = HashSet<(LivingEntity, LivingEntity) -> Boolean>()

    fun addIgnoreAttack(filter: (LivingEntity, LivingEntity) -> Boolean) {
        filters += filter
    }


    /**
     * Entity attack cal
     *
     * @param key
     * @param data
     * @return
     */
    fun runFight(key: String, data: FightData, message: Boolean = true, damage: Boolean = true): Double {
        return fightGroupManager.runFight(key, data, message, damage)
    }

    /**
     * 实体是否在战斗
     *
     * @return Boolean 是否在战斗
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.isFighting(): Boolean {
        return FightSystem.fightStatusManager.isFighting(this)
    }

    /**
     * 让实体进入战斗
     *
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.intoFighting() {
        FightSystem.fightStatusManager.intoFighting(this)
    }

    /**
     * 让实体退出战斗状态
     *
     * @receiver LivingEntity 实体
     */

    @JvmStatic
    fun LivingEntity.outFighting() {
        FightSystem.fightStatusManager.outFighting(this)
    }
}
