package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.api.manager.RealizerManager
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object RealizerManagerImpl : RealizerManager() {
    override val priority: Int = 999

    @Awake(LifeCycle.DISABLE)
    fun disable() {
        onDisable()
    }
}