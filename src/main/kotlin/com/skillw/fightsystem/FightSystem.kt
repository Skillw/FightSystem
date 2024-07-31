package com.skillw.fightsystem

import com.skillw.fightsystem.api.manager.*
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.plugin.annotation.PouManager
import org.apache.logging.log4j.core.util.Loader
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
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

    @JvmStatic
    @PouManager
    lateinit var realizerManager: RealizerManager

    override fun onLoad() {
        load()
    }

    //给forgeMod兼容留的位置
    private val forge by lazy {
        Loader.isClassAvailable("net.minecraftforge.eventbus.api.SubscribeEvent")
    }

    override fun onEnable() {
        enable()
        if (forge) {
            //.....一些MinecraftForge的操作
        }
    }

    override fun onActive() {
        active()
    }

    override fun onDisable() {
        disable()
    }

    fun debug(string: String) {
        if (com.skillw.fightsystem.FightSystem.configManager.debug) {
            console().sendMessage(string.colored())
        }
    }

    fun debugLang(path: String, vararg args: String) {
        if (com.skillw.fightsystem.FightSystem.configManager.debug) {
            console().sendLang(path, *args)
        }
    }

}
