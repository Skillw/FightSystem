package com.skillw.fightsystem.internal.manager

import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.message.MessageBuilder
import com.skillw.fightsystem.api.manager.MessageBuilderManager
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap


/**
 * MessageType type manager
 *
 * @constructor Create empty MessageType type manager
 */
object MessageBuilderManagerImpl : MessageBuilderManager() {
    override val key = "MessageBuilderManager"
    override val priority: Int = 3
    override val subPouvoir = com.skillw.fightsystem.FightSystem
    override val attack = LowerKeyMap<MessageBuilder>()
    override val defend = LowerKeyMap<MessageBuilder>()
}
