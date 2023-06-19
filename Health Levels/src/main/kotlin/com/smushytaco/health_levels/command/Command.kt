package com.smushytaco.health_levels.command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import com.smushytaco.health_levels.abstractions.HealthMethods.onModified
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
object Command {
    private fun getter(response: (HealthLevelsXP) -> Text, literal: String = "check"): LiteralArgumentBuilder<ServerCommandSource> {
        return LiteralArgumentBuilder.literal<ServerCommandSource>(literal)
            .executes { ctx ->
                ctx.source.sendFeedback({ response(ctx.source.player as HealthLevelsXP) }, false)
                return@executes 0
            }
            .then(CommandManager.argument("player", EntityArgumentType.player())
                .requires { it.hasPermissionLevel(2) }
                .executes { ctx ->
                    ctx.source.sendFeedback({ response(EntityArgumentType.getPlayer(ctx, "player") as HealthLevelsXP) }, true)
                    return@executes 0
                })
    }
    private fun setter(literal: String, set: (PlayerEntity, Int) -> Unit): LiteralArgumentBuilder<ServerCommandSource> {
        return CommandManager.literal(literal)
            .requires { it.hasPermissionLevel(2) }
            .then(CommandManager.argument("players", EntityArgumentType.players())
                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                    .executes { ctx ->
                        val players = EntityArgumentType.getPlayers(ctx, "players")
                        val amount = IntegerArgumentType.getInteger(ctx, "amount")
                        players.map { it }.forEach { set(it, amount) }
                        return@executes 0
                    }
                )
            )
    }
    fun buildHealthLevelsCommand(): LiteralArgumentBuilder<ServerCommandSource> {
        val base = CommandManager.literal("healthlevels")
        listOf(
            setter("setxp") { target, healthXP ->
                if (target !is HealthLevelsXP) return@setter
                target.healthXP = healthXP
                target.onModified()
            },
            setter("addxp") { target, healthXP ->
                if (target !is HealthLevelsXP) return@setter
                target.healthXP += healthXP
                target.onModified()
            },
            setter("setlevel") { target, healthLevel ->
                if (target !is HealthLevelsXP) return@setter
                target.healthLevel = healthLevel
                target.onModified()
            },
            setter("addlevel") { target, healthLevels ->
                if (target !is HealthLevelsXP) return@setter
                target.healthLevel += healthLevels
                target.onModified()
            },
            getter({ target -> Text.literal("Level: ${target.healthLevel}\nXP: ${target.healthXP}/${HealthLevels.config.levelsAndXP[target.healthLevel.coerceAtMost(HealthLevels.config.levelsAndXP.size - 1)]}") })).forEach { subCmd -> base.then(subCmd) }
        return base
    }
}