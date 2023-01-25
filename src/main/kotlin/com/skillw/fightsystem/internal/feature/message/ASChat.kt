package com.skillw.fightsystem.internal.feature.message

import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.message.Message
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import org.bukkit.entity.Player
import taboolib.module.chat.colored

class ASChat(val text: StringBuilder, override val fightData: FightData) : Message {

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASChat
        text.append(separator(type)).append(message.text)
        return this
    }

    fun separator(type: Message.Type): String {
        return FSConfig["message"].getString("fight-message.chat.${type.name.lowercase()}.separator") ?: "&5|"
    }

    override fun sendTo(vararg players: Player) {
        players.forEach { player -> player.sendMessage(text.toString().placeholder(player).colored()) }
    }

}
