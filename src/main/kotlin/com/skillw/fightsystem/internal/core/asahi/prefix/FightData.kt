package com.skillw.fightsystem.internal.core.asahi.prefix


import com.skillw.asahi.api.annotation.AsahiPrefix
import com.skillw.asahi.api.prefixParser
import com.skillw.asahi.api.quest
import com.skillw.asahi.api.quester
import com.skillw.attsystem.AttributeSystem.operationManager
import com.skillw.attsystem.api.read.operation.NumberOperation
import com.skillw.fightsystem.FightSystem.damageTypeManager
import com.skillw.fightsystem.api.fight.FightData

@AsahiPrefix(["fight_data"], "fightsystem")
private fun fightData() = prefixParser {
    val dataGetter = if (expect("of")) quest<FightData>() else quester { selector() }
    when (val type = next()) {
        "damage" -> {
            when (val token = next()) {
                "type" -> {
                    val damageType = questString().quester { damageTypeManager[it]!! }
                    when (val token2 = next()) {

                        "put" -> {
                            val key = quest<String>()
                            expect("with")
                            val operationKey = quest<String>()
                            expect("to")
                            val value = quest<Double>()
                            result {
                                val operation = operationManager[operationKey.get()] as? NumberOperation?
                                    ?: error("parse Number Operation Error")
                                dataGetter.get().let { data ->
                                    data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                        .damageSources[key.get()] = operation.element(value.get())
                                }
                            }
                        }

                        "remove" -> {
                            val key = quest<String>()
                            result {
                                dataGetter.get().let { data ->
                                    data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                        .damageSources.remove(key.get())
                                }
                            }
                        }

                        "has" -> {
                            val key = quest<String>()
                            result {

                                dataGetter.get().let { data ->
                                    data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                        .damageSources.containsKey(key.get())
                                }
                            }
                        }

                        "get" -> {
                            val key = quest<String>()
                            result {

                                dataGetter.get().let { data ->
                                    data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                        .damageSources[key.get()]
                                }
                            }
                        }

                        "size" -> result {
                            dataGetter.get().let { data ->
                                data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                    .damageSources.size
                            }
                        }

                        "clear" -> result {
                            dataGetter.get().let { data ->
                                data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                    .damageSources.clear()
                            }
                        }

                        "isEmpty" -> result {
                            dataGetter.get().let { data ->
                                data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                    .damageSources.isEmpty()
                            }
                        }

                        "keys" -> result {
                            dataGetter.get().let { data ->
                                data.damageTypes.computeIfAbsent(damageType.get()) { FightData(data) }
                                    .damageSources.keys
                            }
                        }

                        else -> error("Invalid Data Type Damage Action Type $token2")
                    }
                }

                "put" -> {
                    val key = quest<String>()
                    expect("with")
                    val operationKey = quest<String>()
                    expect("to")
                    val value = quest<Double>()
                    result {
                        val operation = operationManager[operationKey.get()] as? NumberOperation?
                            ?: error("parse Number Operation Error")
                        dataGetter.get().damageSources[key.get()] = operation.element(value.get())
                    }
                }

                "remove" -> {
                    val key = quest<String>()
                    result {

                        dataGetter.get().damageSources.remove(key.get())
                    }
                }

                "has" -> {
                    val key = quest<String>()
                    result {

                        dataGetter.get().damageSources.containsKey(key.get())
                    }
                }

                "get" -> {
                    val key = quest<String>()
                    result {

                        dataGetter.get().damageSources[key.get()]
                    }
                }

                "size" -> result {

                    dataGetter.get().damageSources.size
                }

                "clear" -> result {

                    dataGetter.get().damageSources.clear()
                }

                "isEmpty" -> result {

                    dataGetter.get().damageSources.isEmpty()
                }

                "keys" -> result {

                    dataGetter.get().damageSources.keys
                }

                else -> error("Invalid Data Damage Action Type $token")
            }
        }

        "hasResult" ->
            if (expect("=", "to")) {
                val bool = quest<Boolean>()
                result {

                    dataGetter.get().hasResult = bool.get()
                }
            } else result {

                dataGetter.get().hasResult
            }

        "calMessage" ->
            if (expect("=", "to")) {
                val bool = quest<Boolean>()
                result {

                    dataGetter.get().calMessage = bool.get()
                }
            } else result {

                dataGetter.get().calMessage
            }

        "put" -> {
            val key = quest<String>()
            expect("to")
            val value = quest<Any>()
            result { dataGetter.get().put(key.get(), value.get()) }
        }

        "get" -> {
            val key = quest<String>()
            result { dataGetter.get()[key.get()] }
        }

        "remove" -> {
            val key = quest<String>()
            result { dataGetter.get().remove(key.get()) }
        }

        "has" -> {
            val key = quest<String>()
            result { dataGetter.get().containsKey(key.get()) }
        }

        "size" -> {
            result { dataGetter.get().size }
        }

        "clear" -> {
            result { dataGetter.get().clear() }
        }

        "isEmpty" -> {
            result { dataGetter.get().isEmpty() }
        }

        "keys" -> {
            result { dataGetter.get().keys }
        }

        else -> {
            error("Invalid Data token $type")
        }
    }
}