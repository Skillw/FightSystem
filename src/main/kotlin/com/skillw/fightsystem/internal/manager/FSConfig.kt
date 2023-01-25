package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.manager.ConfigManager
import com.skillw.pouvoir.api.plugin.map.BaseMap
import com.skillw.pouvoir.util.static
import org.bukkit.Bukkit
import org.spigotmc.AsyncCatcher
import taboolib.common.platform.Platform
import taboolib.common.platform.function.getDataFolder
import taboolib.module.metrics.charts.SingleLineChart
import java.io.File
import java.util.regex.Pattern

object FSConfig : ConfigManager(FightSystem) {
    override val priority = 0

    var lineConditionPattern: Pattern = Pattern.compile("options.condition.line-condition.format")
    val defaultRegainHolo: Boolean
        get() = this["message"].getBoolean("options.default.health-regain-holo")


    override fun onLoad() {
        AsyncCatcher.enabled = false
        createIfNotExists("fight_group", "default.yml", "skapi.yml", "mythic_skill.yml", "damage_event.yml")
        createIfNotExists("damage_type", "magic.yml", "physical.yml", "real.yml")
        createIfNotExists(
            "scripts",
            "mechanics/basic.js",
            "mechanics/mechanics.js",
            "mechanics/mythicskill.js",
            "mechanics/runner.js",
            "mechanics/shield.js",
        )
        //兼容1.4.3及之前的脚本
        mapOf(
            "com.skillw.fightsystem.internal.fight" to "com.skillw.fightsystem.internal.core.fight",
        ).forEach(Pouvoir.scriptEngineManager::relocate)

        Pouvoir.scriptEngineManager.globalVariables.let {
            it["FightSystem"] = FightSystem::class.java.static()
        }
    }

    override fun onEnable() {
        onReload()
        val metrics =
            taboolib.module.metrics.Metrics(
                14465,
                FightSystem.plugin.description.version,
                Platform.BUKKIT
            )
        metrics.addCustomChart(SingleLineChart("FightGroups") {
            FightSystem.fightGroupManager.size
        })
        metrics.addCustomChart(SingleLineChart("mechanics") {
            FightSystem.mechanicManager.size
        })
    }


    val attackFightKeyMap = BaseMap<String, String>()
    override fun subReload() {
        Pouvoir.scriptManager.addScriptDir(scripts)

        attackFightKeyMap.clear()
        this["config"].getConfigurationSection("options.fight.attack-fight")?.apply {
            getKeys(false).forEach { key: String ->
                attackFightKeyMap[key] = getString(key) ?: return@forEach
            }
        }
    }

    val skillAPI by lazy {
        Bukkit.getPluginManager().isPluginEnabled("SkillAPI") || Bukkit.getPluginManager()
            .isPluginEnabled("ProSkillAPI")
    }

    val mythicMobs by lazy {
        Bukkit.getPluginManager().isPluginEnabled("MythicMobs")
    }

    private val scripts = File(getDataFolder(), "scripts")
    val isPersonalEnable
        get() = this["message"].getBoolean("options.personal")

    val defaultAttackMessageType: String
        get() = (this["message"].getString("options.default.attack") ?: "HOLO").uppercase()

    val defaultDefendMessageType: String
        get() = (this["message"].getString("options.default.defend") ?: "CHAT").uppercase()


    val isVanillaArmor: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-armor")

    var isDebug = false

    val debug: Boolean
        get() = this["config"].getBoolean("options.debug") || isDebug

    val eveFightCal
        get() = this["config"].getBoolean("options.fight.eve-fight-cal")

    val defaultAttackerName: String
        get() = this["config"].getString("fight-message.default-name.attacker") ?: "大自然"
    val defaultDefenderName: String
        get() = this["config"].getString("fight-message.default-name.defender") ?: "未知"

    val arrowCache
        get() = this["config"].getBoolean("options.fight.arrow-cache-data", true)

    @JvmStatic
    fun debug(debug: () -> Unit) {
        if (this.debug) {
            debug.invoke()
        }
    }
}
