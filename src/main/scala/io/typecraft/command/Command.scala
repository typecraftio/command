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
import scala.collection.immutable

sealed trait Command

object Command {
  type CmdMap = immutable.Map[String, Command]
  type CmdMapR[F[_]] = Ref[F, CmdMap]

  def map(cmds: CmdMap): Command = Map(cmds)

  def effect(thunk: => Unit): Command = Effect(() => thunk)

  def run[F[_], A](cmd: Command)(
      args: Chain[String]
  )(a: A)(implicit F: Sync[F]): F[Either[CommandError, Unit]] =
    cmd match {
      case Map(map) =>
        next(args)(map) match {
          case Left(error) =>
            F.pure(Left(error))
          case Right((subCmd, tails)) =>
            run(subCmd)(tails)(F)
        }
      case Effect(thunk) =>
        F.delay(Right(thunk()))
    }

  def runNext[F[_], A](cmdsR: CmdMapR[F])(implicit
      S: CommandSource[F, A],
      F: Sync[F]
  ): F[Unit] = for {
    input <- S.nextCommandInput
    cmds <- cmdsR.get
    val (sender, args) = input
    _ <- next(args)(cmds) match {
      case Left(error) =>
        ???
      case Right((cmd, tails)) =>
        run(cmd)(tails)(sender).attempt
    }
  } yield ()

  def runLoop[F[_], A](cmdsR: CmdMapR[F])(implicit
      S: CommandSource[F, A],
      F: Sync[F]
  ): F[Unit] = runNext(cmdsR).foreverM

  def startLoop[F[_], A](cmdsR: CmdMapR[F])(implicit
      S: CommandSource[F, A],
      F: Async[F]
  ): F[Fiber[F, Throwable, Unit]] = F.start(runLoop(cmdsR))

  def parse[A, B](args: Chain[String])(a: A): Either[CommandError, B] =
    ???

  private def next(
      args: Chain[String]
  )(cmds: CmdMap): Either[CommandError, (Command, Chain[String])] = {
    for {
      cons <- args.uncons.toRight(CommandError.NoArgument)
      val (head, tail) = cons
      cmd <- cmds.get(head).toRight(CommandError.NoCommand)
    } yield (cmd, tail)
  }

  private case class Map(
      private val cmds: immutable.Map[String, Command]
  ) extends Command
  private case class Effect(
      private val thunk: () => Unit
  ) extends Command
}
