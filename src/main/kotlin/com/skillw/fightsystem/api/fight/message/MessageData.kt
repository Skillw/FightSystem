package com.skillw.fightsystem.api.fight.message

import com.skillw.fightsystem.api.fight.message.Message.Companion.send
import org.bukkit.entity.Player

/**
 * @className MessageData
 *
 * @author Glom
 * @date 2022/7/11 17:37 Copyright 2022 user. All rights reserved.
 */
class MessageData {
    /** Attack messages */
    val attackMessages = ArrayList<Message>()

    /** Defend messages */
    val defendMessages = ArrayList<Message>()

    /**
     * Send
     *
     * @param attacker
     * @param defender
     */
    fun send(attacker: Player?, defender: Player?) {
        attacker?.also { attackMessages.send(Message.Type.ATTACK, attacker) }
        defender?.also { defendMessages.send(Message.Type.DEFEND, defender) }
    }

    /**
     * Add all
     *
     * @param other
     * @return
     */
    fun addAll(other: MessageData): MessageData {
        this.attackMessages.addAll(other.attackMessages)
        this.defendMessages.addAll(other.defendMessages)
        return this
    }
}