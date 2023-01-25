package com.skillw.fightsystem.internal.command.sub

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.util.soundClick
import com.skillw.pouvoir.util.soundSuccess
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang

object PersonalCommand {

    val personal = subCommand {
        dynamic {
            suggestion<Player> { sender, _ ->
                sender.soundClick()
                listOf("AttackingMessageType", "DefensiveMessageType", "RegainHolo")
            }
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.soundClick()
                    when (context.argument(-1)) {
                        "AttackingMessageType" -> com.skillw.fightsystem.FightSystem.messageBuilderManager.attack.keys.map { it.uppercase() }
                            .toMutableList()
                            .apply { add("DISABLE") }

                        "DefensiveMessageType" -> com.skillw.fightsystem.FightSystem.messageBuilderManager.defend.keys.map { it.uppercase() }
                            .toMutableList()
                            .apply { add("DISABLE") }

                        "RegainHolo" -> listOf("true", "false")
                        else -> emptyList()
                    }
                }
                execute<Player> { sender, context, argument ->
                    if (!FSConfig.isPersonalEnable) return@execute
                    val data = com.skillw.fightsystem.FightSystem.personalManager[sender.uniqueId]!!
                    val type = context.argument(-1)
                    var typeMessage: String
                    when (type) {
                        "AttackingMessageType" -> data.attacking = argument
                            .also { typeMessage = sender.asLangText("attacking-message-type") }

                        "DefensiveMessageType" -> data.defensive = argument
                            .also { typeMessage = sender.asLangText("defensive-message-type") }

                        "RegainHolo" -> data.regainHolo =
                            Coerce.toBoolean(argument).also { typeMessage = sender.asLangText("regain-holo-type") }

                        else -> return@execute
                    }
                    val subTypeMessage = sender.asLangText("type-${argument.replace("_", "-")}")
                    sender.soundSuccess()
                    com.skillw.fightsystem.FightSystem.personalManager[sender.uniqueId] = data
                    sender.sendLang("command-personal", typeMessage, subTypeMessage)
                }
            }
        }
    }
}