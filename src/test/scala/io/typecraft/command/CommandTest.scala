package io.typecraft.command

import org.scalatest.FunSuite
import io.typecraft.command.kernel.CommandSource
import cats.effect.IO
import cats.data.Chain
import CommandTest.pureInput
import Command.CmdMapR
import cats.effect.SyncIO
import cats.effect.kernel.Ref

class CommandTest extends FunSuite {
  type Commander = String
  test("root") {
    val source = pureInput[String]("", Chain("mycmd"))
    val cmds = Map(("mycmd", Command.effect(println("Hi!"))))
  }
}

object CommandTest {
  def pureInput[A](a: A, args: Chain[String]): CommandSource[IO, A] =
    new CommandSource[IO, A] {
      override def nextCommandInput: IO[(A, Chain[String])] =
        IO.pure((a, args))
    }

  def cmdsRef(map: Map[String, Command]): CmdMapR[SyncIO] =
    Ref.unsafe(map)
}
