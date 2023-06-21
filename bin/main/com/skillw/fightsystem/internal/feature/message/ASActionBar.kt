package com.skillw.fightsystem.internal.feature.message

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.FightData
import com.skillw.fightsystem.api.fight.message.Message
import com.skillw.fightsystem.internal.manager.FSConfig
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import com.skillw.pouvoir.util.sendActionBar
import org.bukkit.entity.Player
import taboolib.module.chat.colored

class ASActionBar(
    private val text: StringBuilder, override val fightData: FightData,
) : Message {


    fun separator(type: Message.Type): String {
        return FSConfig["message"].getString("fight-message.action-bar.${type.name.lowercase()}.separator") ?: "&5|"
    }

    private fun append(text: StringBuilder, type: Message.Type): ASActionBar {
        this.text.append(separator(type)).append(text)
        return this
    }


    override fun sendTo(vararg players: Player) {
        players.forEach { player ->
            sendActionBar(
                player,
                text.toString().placeholder(player).colored(),
                FSConfig["message"].getLong("fight-message.action-bar.stay"),
                com.skillw.fightsystem.FightSystem.plugin
            )
        }
    }

    fun sendToInfo(player: Player) {
        sendActionBar(
            player,
            text.toString().placeholder(player)
        )
    }

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASActionBar
        return this.append(message.text, type)
    }

}
