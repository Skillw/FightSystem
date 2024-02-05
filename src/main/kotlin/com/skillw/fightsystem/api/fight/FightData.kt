package com.skillw.fightsystem.api.fight

import com.skillw.asahi.api.AsahiAPI.analysis
import com.skillw.asahi.api.AsahiAPI.asahi
import com.skillw.asahi.api.member.context.AsahiContext
import com.skillw.asahi.api.member.namespace.NamespaceContainer
import com.skillw.asahi.api.member.namespace.NamespaceHolder
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.feature.compat.pouvoir.AttributePlaceHolder
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.message.MessageData
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.feature.operation.OperationElement
import com.skillw.pouvoir.util.parse
import com.skillw.pouvoir.util.script.ColorUtil.decolored
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import taboolib.common.util.asList
import taboolib.common5.Coerce
import taboolib.module.chat.uncolored
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * Fight data
 *
 * @constructor Create empty Fight data
 * @property attacker 攻击者
 * @property defender 防御者
 */
class FightData(attacker: LivingEntity?, defender: LivingEntity?, vararg namespaces: String) :
    NamespaceHolder<FightData>,
    AsahiContext by AsahiContext.create(ConcurrentHashMap()) {
    constructor(attacker: LivingEntity?, defender: LivingEntity?, run: Consumer<FightData>) : this(attacker, defender) {
        run.accept(this)
    }

    constructor(attacker: LivingEntity?, defender: LivingEntity?) : this(attacker, defender, namespaces = emptyArray())

    override val namespaces = NamespaceContainer().apply { addNamespaces(*namespaces) }

    var cache = DataCache(this)
        set(value) {
            field.variables.keys.forEach(this::remove)
            field = value
            value.data = this
            putAll(value.variables)
        }
    var attacker: LivingEntity? = null
        set(value) {
            field = value
            cache.attacker(value)
        }
    var defender: LivingEntity? = null
        set(value) {
            field = value
            cache.defender(value)
        }

    fun attackerData(attKey: String, params: List<String>) {
        cache.attackerData(attKey, params)
    }

    fun defenderData(attKey: String, params: List<String>) {
        cache.defenderData(attKey, params)
    }

    val attackerData: AttributeDataCompound
        get() = cache.attackerData ?: AttributeDataCompound()
    val defenderData: AttributeDataCompound
        get() = cache.defenderData ?: AttributeDataCompound()

    init {
        this.attacker = attacker
        this.defender = defender
    }

    var event: Cancellable? = null
        set(value) {
            field = value
            this["event"] = value!!
        }
        get() {
            return field ?: this["event"] as? Cancellable?
        }

    /** MessageType data */
    val messageData = MessageData()

    /** Damage sources */
    val damageSources = LinkedHashMap<String, OperationElement>()

    var damageTypes = LinkedHashMap<DamageType, FightData>()

    /** Has result */
    var hasResult = true

    /** Cal message */
    var calMessage = true

    /**
     * Cal result
     *
     * @return result
     */
    fun calResult(): Double {
        if (!hasResult) return 0.0
        var result = 0.0
        damageTypes.values.forEach {
            result += it.calResult()
        }
        damageSources.values.forEach {
            result = it.operate(result).toDouble()
        }
        return result
    }

    fun calMessage() {
        FightSystem.debugLang("fight-info-message")
        damageTypes.forEach { (type, fightData) ->
            fightData["result"] = fightData.calResult()
            if (attacker is Player && calMessage)
                type.attackMessage(attacker as Player, fightData, messageData.attackMessages.isEmpty())
                    ?.also { messageData.attackMessages.add(it) }
            if (defender is Player && calMessage) {
                type.defendMessage(defender as Player, fightData, messageData.defendMessages.isEmpty())
                    ?.also { messageData.defendMessages.add(it) }
            }
        }
    }


    constructor(fightData: FightData) : this(fightData.attacker, fightData.defender) {
        this.cache = fightData.cache
        this.namespaces.addNamespaces(fightData.namespaces)
        putAll(fightData)
    }


    /**
     * Handle map
     *
     * @param map
     * @param K
     * @param V
     * @return
     */
    fun handleMap(map: Map<*, *>, log: Boolean = true): Map<String, Any> {
        val newMap = ConcurrentHashMap<String, Any>()
        map.forEach { (key, value) ->
            if (log)
                FSConfig.debug { FightSystem.debug("      &e$key&5:") }
            newMap[key.toString()] = handle(value ?: return@forEach, log)
        }
        return newMap
    }

    override fun toString(): String {
        return "FightData { Types: $damageTypes , DamageSources: $damageSources }"
    }

    /**
     * 解析Any
     *
     * 给脚本用的
     *
     * @param any 字符串/字符串集合/Map
     * @return 解析后的Any
     */
    fun handle(any: Any): Any {
        return handle(any, true)
    }

    /**
     * 解析Any
     *
     * @param any 字符串/字符串集合/Map
     * @return 解析后的Any
     */
    fun handle(any: Any, log: Boolean = true): Any {
        if (any is String) {
            return handleStr(any, log)
        }
        if (any is List<*>) {
            if (any.isEmpty()) return "[]"
            if (any[0] is Map<*, *>) {
                val mapList = Coerce.toListOf(any, Map::class.java)
                val newList = LinkedList<Map<*, *>>()
                mapList.forEach {
                    newList.add(handleMap(it))
                }
                return newList
            }
            return handleList(any.asList(), log)
        }
        if (any is Map<*, *>) {
            return handleMap(any)
        }
        return any
    }

    private fun String.attValue(entity: LivingEntity, data: AttributeDataCompound): String {
        val placeholder = substring(3)
        return AttributePlaceHolder.placeholder(placeholder, entity, data)
    }

    private fun String.placeholder(
        str: String,
        entity: LivingEntity,
        data: AttributeDataCompound,
        log: Boolean = true,
    ): String {
        val placeholder = str.substring(2)
        val value = if (placeholder.startsWith("as_")) placeholder.attValue(
            entity,
            data
        ) else Pouvoir.placeholderManager.replace(entity, "%${placeholder}%")
        if (log)
            FSConfig.debug {
                FightSystem.debug(
                    "       &3{${str.uncolored().decolored()}} &7-> &9${
                        value.uncolored().decolored()
                    }"
                )
            }
        return replace(
            "{$str}",
            value
        )
    }

    /**
     * Handle
     *
     * @param string 待解析字符串
     * @return 解析后的字符串
     */
    fun handleStr(string: String, log: Boolean = true): String {
        val event = com.skillw.fightsystem.api.event.FightDataHandleEvent(this, string)
        event.call()
        var formula = event.string
        val list = formula.parse('{', '}')
        for (str in list) {
            when {
                str.startsWith("a.") -> {
                    formula = formula.placeholder(str, attacker!!, attackerData, log)
                    continue
                }

                str.startsWith("d.") -> {
                    formula = formula.placeholder(str, defender!!, defenderData, log)
                }

                else -> {
                    val replacement = this[str] ?: continue
                    formula = formula.replace("{$str}", replacement.toString())
                    if (log)
                        FSConfig.debug {
                            FightSystem.debug(
                                "       &3{${str.uncolored().decolored()}} &7-> &9${
                                    replacement.toString().uncolored().decolored()
                                }"
                            )
                        }
                    continue
                }
            }
        }
        val value = formula.run {
            when {
                startsWith("!") -> substring(1)

                else -> asahi(
                    namespaces = namespaceNames(),
                    context = this@FightData
                ).toString().analysis(this@FightData)
            }
        }

        if (log) FSConfig.debug {
            FightSystem.debug(
                "      &3${formula.uncolored().decolored()} &7-> &9${
                    value.uncolored().decolored()
                }"
            )
        }
        return value
    }

    /**
     * 解析
     *
     * @param strings 待解析的字符串集
     * @return 解析后的字符串集
     */
    fun handleList(strings: Collection<String>, log: Boolean = true): List<String> {
        val list = ArrayList<String>()
        strings.forEach {
            list.add(handleStr(it, log))
        }
        return list
    }

    override fun clone(): AsahiContext {
        return FightData(attacker, defender) {
            it.putAll(this)
        }
    }

    override fun context(): AsahiContext {
        return this
    }


}