package com.skillw.fightsystem

import com.skillw.fightsystem.api.manager.*
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.plugin.annotation.PouManager
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin

object FightSystem : Plugin(), SubPouvoir {

    override val key = "FightSystem"
    override lateinit var managerData: ManagerData
    override val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    /** Configs */

    @Config("config.yml", migrate = true, autoReload = true)
    lateinit var config: ConfigFile

    @Config("message.yml", migrate = true, autoReload = true)
    lateinit var message: ConfigFile

    @Config("options.yml", migrate = true, autoReload = true)
    lateinit var options: ConfigFile

    /** Managers */

    @JvmStatic
    @PouManager
    lateinit var configManager: FSConfig

    @JvmStatic
    @PouManager
    lateinit var damageTypeManager: DamageTypeManager

    @JvmStatic
    @PouManager
    lateinit var mechanicManager: MechanicManager

    @JvmStatic
    @PouManager
    lateinit var fightGroupManager: FightGroupManager

    @JvmStatic
    @PouManager
    lateinit var personalManager: PersonalManager

    @JvmStatic
    @PouManager
    lateinit var messageBuilderManager: MessageBuilderManager


    @JvmStatic
    @PouManager
    lateinit var fightStatusManager: FightStatusManager
    override fun onLoad() {
        load()
    }

    override fun onEnable() {
        enable()
    }

    override fun onActive() {
        active()
    }

    override fun onDisable() {
        disable()
    }

    fun debug(string: String) {
        if (com.skillw.fightsystem.FightSystem.configManager.debug) {
            info(string.colored())
        }
    }

    fun debugLang(path: String, vararg args: String) {
        if (com.skillw.fightsystem.FightSystem.configManager.debug) {
            console().sendLang(path, *args)
        }
    }

}
