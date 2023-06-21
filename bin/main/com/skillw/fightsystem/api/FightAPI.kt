package com.skillw.fightsystem.api

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.FightSystem.fightGroupManager
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.feature.listener.fight.Attack
import org.bukkit.entity.LivingEntity

/**
 * Fight API
 *
 * 提供了一些拓展函数，用于快速调用API
 *
 * @constructor Create empty Fight a p i
 */

object FightAPI {

    /** 跳过下次战斗组计算 */
    fun skipNextDamageCal() {
        Attack.nextAttackCal = true
    }


    /**
     * Entity attack cal
     *
     * @param key
     * @param data
     * @return
     */
    fun runFight(key: String, data: FightData, message: Boolean = true): Double {
        return fightGroupManager.runFight(key, data, message)
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
