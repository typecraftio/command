package io.typecraft.command

trait CommandError

object CommandError {
    object NoArgument extends CommandError
    object NoCommand extends CommandError
}
