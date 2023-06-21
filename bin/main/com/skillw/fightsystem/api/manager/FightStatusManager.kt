package com.skillw.fightsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * Fight status manager
 *
 * @constructor Create empty Fight status manager
 */
abstract class FightStatusManager : Manager {
    /**
     * Is fighting
     *
     * @param uuid
     * @return
     */
    abstract fun isFighting(uuid: UUID): Boolean

    /**
     * Is fighting
     *
     * @param entity
     * @return
     */
    abstract fun isFighting(entity: LivingEntity): Boolean

    /**
     * Into fighting
     *
     * @param entity
     */
    abstract fun intoFighting(entity: LivingEntity)

    /**
     * Into fighting
     *
     * @param uuid
     */
    abstract fun intoFighting(uuid: UUID)

    /**
     * Out fighting
     *
     * @param entity
     */
    abstract fun outFighting(entity: LivingEntity)

    /**
     * Out fighting
     *
     * @param uuid
     */
    abstract fun outFighting(uuid: UUID)

}
