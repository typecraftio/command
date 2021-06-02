package io.typecraft.command.kernel

import cats.data.Chain
import CommandSource.Input

trait CommandSource[F[_], A] {
  def nextCommandInput: F[(A, Chain[String])]
}

object CommandSource {
  type Input[A] = (A, Chain[String])
}
