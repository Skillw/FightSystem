package com.skillw.fightsystem.internal.command

import com.skillw.attsystem.AttributeSystem
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.internal.command.sub.MirrorCommand
import com.skillw.fightsystem.internal.command.sub.PersonalCommand
import com.skillw.fightsystem.internal.manager.FSConfig.isDebug
import com.skillw.pouvoir.util.soundClick
import com.skillw.pouvoir.util.soundFail
import com.skillw.pouvoir.util.soundSuccess
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.pluginVersion
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

@CommandHeader(name = "fs", permission = "fs.command")
object FSCommand {
    internal fun ProxyCommandSender.soundSuccess() {
        (this.origin as? Player?)?.soundSuccess()
    }

    internal fun ProxyCommandSender.soundFail() {
        (this.origin as? Player?)?.soundFail()
    }

    internal fun ProxyCommandSender.soundClick() {
        (this.origin as? Player?)?.soundClick()
    }

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("command-info")
            sender.soundSuccess()
        }
        incorrectCommand { sender, _, _, _ ->
            sender.sendLang("wrong-command")
            sender.soundFail()
        }
        incorrectSender { sender, _ ->
            sender.sendLang("wrong-sender")
            sender.soundFail()
        }
    }

    @CommandBody(permission = "fs.command.help")
    val help = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            sender.sendLang("command-info")
        }
    }

    @CommandBody(permission = "fs.command.info")
    val info = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            sender.sendMessage("&aFightSystem &9v$pluginVersion &6By Glom_".colored())
        }
    }

    @CommandBody(permission = "fs.command.debug")
    val debug = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            isDebug = if (!isDebug) {
                sender.sendMessage("&aDebug on!".colored())
                true
            } else {
                sender.sendMessage("&aDebug off!".colored())
                false
            }
        }
    }

    @CommandBody(permission = "fs.command.reload")
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            AttributeSystem.reload()
            FightSystem.reload()
            sender.sendLang("command-reload")
        }
    }

    @CommandBody(permission = "fs.command.report")
    val report = MirrorCommand.report

    @CommandBody(permission = "fs.command.clear")
    val clear = MirrorCommand.clear


    @CommandBody(permission = "fs.command.personal")
    val personal = PersonalCommand.personal
}
