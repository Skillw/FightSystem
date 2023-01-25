package com.skillw.fightsystem.internal.core.fight

import com.skillw.asahi.api.AsahiManager
import com.skillw.asahi.api.member.namespace.Namespace
import com.skillw.asahi.api.member.namespace.NamespaceHolder
import com.skillw.fightsystem.FightSystem.debugLang
import com.skillw.fightsystem.api.event.DamageTypeRunEvent
import com.skillw.fightsystem.api.fight.DamageType
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.api.plugin.map.LinkedKeyMap
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import org.bukkit.configuration.serialization.ConfigurationSerializable
import taboolib.common.platform.function.info
import java.util.function.Function

/**
 * Fight group
 *
 * @constructor Create empty Fight group
 * @property key 战斗组键
 */
class FightGroup constructor(
    override val key: String, vararg namespaces: String,
) : Registrable<String>,
    ConfigurationSerializable,
    NamespaceHolder<FightGroup>,
    LinkedKeyMap<DamageType, MechanicDataCompound>() {

    override val namespaces = HashSet<Namespace>().apply {
        addAll(AsahiManager.getNamespaces(*namespaces))
    }

    /** Damage types */
    val damageTypes = this.list

    /**
     * Run
     *
     * @param originData 原战斗数据
     * @return
     */
    internal fun run(originData: FightData): Function<FightData, Double> {
        debugLang("fight-info")
        debugLang("fight-info-key", key)
        debugLang("fight-info-attacker", originData["attacker-name"].toString())
        debugLang("fight-info-defender", originData["defender-name"].toString())

        for (index in damageTypes.indices) {
            val type = damageTypes[index]
            debugLang("fight-info-damage-type", type.name)
            var fightData = FightData(originData)
            val pre = DamageTypeRunEvent.Pre(type, fightData, false)
            pre.call()
            fightData = pre.fightData
            if (!(this[type]!!.run(fightData) || pre.enable)) continue
            val post = DamageTypeRunEvent.Post(type, fightData)
            post.call()
            fightData = post.fightData
            val result = fightData.calResult()
            fightData["result"] = result
            if (originData.damageTypes.containsKey(type)) {
                originData.damageTypes[type]!!.apply {
                    putAll(fightData)
                    damageSources.putAll(fightData.damageSources)
                }
            }

            originData.damageTypes[type] = fightData
            debugLang("fight-info-usable-vars")
            FSConfig.debug {
                fightData.forEach {
                    if (it.key.startsWith("type::")) return@forEach
                    if (it.value::class.java.simpleName.contains("Function", true)) return@forEach
                    info("      type::${type.key}-${it.key} : ${it.value}")
                    originData["type::${type.key}-${it.key}"] = it.value
                }
            }
        }
        return Function {
            val result = it.calResult()
            debugLang("fight-info-result", result.toString())
            result
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        for (damageType in damageTypes) {
            map[damageType.key] = this[damageType]?.serialize() ?: continue
        }
        return map
    }

    companion object {
        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): FightGroup? {
            val key = section.name
            val namespaces = section.getStringList("namespaces").toTypedArray()
            val fightGroup = FightGroup(key, namespaces = namespaces)
            for (damageTypeKey in section.getKeys(false)) {
                val damageType = com.skillw.fightsystem.FightSystem.damageTypeManager[damageTypeKey] ?: continue
                fightGroup[damageType] =
                    MechanicDataCompound.deserialize(section.getConfigurationSection(damageTypeKey)!!) ?: continue
            }
            AsahiManager.loadSharedNamespace(fightGroup)
            return fightGroup
        }
    }

    override fun register() {
        AsahiManager.loadSharedNamespace(this)
        com.skillw.fightsystem.FightSystem.fightGroupManager.register(this)
    }
}
