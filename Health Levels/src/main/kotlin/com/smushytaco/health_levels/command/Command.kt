package com.smushytaco.health_levels.command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import com.smushytaco.health_levels.abstractions.HealthMethods.onModified
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
object Command {
    private fun getter(response: (HealthLevelsXP) -> Component, literal: String = "check"): LiteralArgumentBuilder<CommandSourceStack> {
        return LiteralArgumentBuilder.literal<CommandSourceStack>(literal)
            .executes { ctx ->
                ctx.source.sendSuccess({ response(ctx.source.player as HealthLevelsXP) }, false)
                return@executes 0
            }
            .then(
                Commands.argument("player", EntityArgument.player())
                .requires { it.hasPermission(2) }
                .executes { ctx ->
                    ctx.source.sendSuccess({ response(EntityArgument.getPlayer(ctx, "player") as HealthLevelsXP) }, true)
                    return@executes 0
                })
    }
    private fun setter(literal: String, set: (Player, Int) -> Unit): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal(literal)
            .requires { it.hasPermission(2) }
            .then(
                Commands.argument("players", EntityArgument.players())
                .then(
                    Commands.argument("amount", IntegerArgumentType.integer(0))
                    .executes { ctx ->
                        val players = EntityArgument.getPlayers(ctx, "players")
                        val amount = IntegerArgumentType.getInteger(ctx, "amount")
                        players.map { it }.forEach { set(it, amount) }
                        return@executes 0
                    }
                )
            )
    }
    fun buildHealthLevelsCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        val base = Commands.literal("healthlevels")
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
            getter({ target -> Component.literal("Level: ${target.healthLevel}\nXP: ${target.healthXP}/${HealthLevels.config.levelsAndXP[target.healthLevel.coerceAtMost(HealthLevels.config.levelsAndXP.size - 1)]}") })).forEach { subCmd -> base.then(subCmd) }
        return base
    }
}