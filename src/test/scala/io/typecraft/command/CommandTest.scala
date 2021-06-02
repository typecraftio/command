package io.typecraft.command

import org.scalatest.FunSuite
import io.typecraft.command.kernel.CommandSource
import cats.effect.IO
import cats.data.Chain
import CommandTest.pureInput

class CommandTest extends FunSuite {
  type Commander = String
  test("execution") {
    IO.unit.start
    val source = pureInput[String]("", Chain.empty)
  }
}

object CommandTest {
  def pureInput[A](a: A, args: Chain[String]): CommandSource[IO, A] =
    new CommandSource[IO, A] {
      override def nextCommandInput: IO[(A, Chain[String])] =
        IO.pure((a, args))
    }
}
