package io.typecraft.command

import cats.effect.kernel.Sync
import cats.data.Chain
import cats.effect.kernel.Async
import cats.Monad
import cats.implicits._
import io.typecraft.command.kernel.CommandSource
import cats.FlatMap
import cats.effect.kernel.Fiber
import cats.effect.kernel.Ref

sealed trait Command

object EmptyCommand extends Command

object Command {
  type CmdMapR[F[_]] = Ref[F, Map[String, Command]]

  def execute[F[_], A](cmd: Command)(
      args: Chain[String]
  )(a: A)(implicit F: Sync[F]): F[Unit] =
    cmd match { // TODO: cmd
      case _ => F.unit
    }

  def runLoop[F[_], A](cmdsR: CmdMapR[F])(implicit
      S: CommandSource[F, A],
      F: Sync[F]
  ): F[Unit] = (for {
    input <- S.nextCommandInput
    cmds <- cmdsR.get
    val (sender, args) = input
    val consO = for {
      cons <- args.uncons
      val (head, tail) = cons
      cmd <- cmds.get(head)
    } yield (cmd, tail)
    _ <- consO match {
      case Some((cmd, tail)) => execute(cmd)(tail)(sender)
      case None => F.unit
    }
  } yield ()).foreverM

  def startLoop[F[_], A](cmdsR: CmdMapR[F])(implicit
      S: CommandSource[F, A],
      F: Async[F]
  ): F[Fiber[F, Throwable, Unit]] = F.start(runLoop(cmdsR))

  type CommandError = String

  def parse[A, B](args: Chain[String])(a: A): Either[CommandError, B] =
    ???
}
