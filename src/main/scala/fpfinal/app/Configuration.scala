package fpfinal.app

import cats.data._
import fpfinal.common.IO
import fpfinal.service._

/**
 * Common types and values used throughout the application.
 */
object Configuration {
  type IsValid[A] = Validated[NonEmptyChain[String], A]
  type Error = String
  type ErrorOr[A] = EitherT[IO, Error, A] // stands for IO[Either[Error, A]]
  type St[A] = StateT[ErrorOr, AppState, A] // stands for State[AppState, ErrorOr[A]]
//  type St1[A] = StateT[ErrorOr, AddPersonData, A] // stands for State[AppState, ErrorOr[A]]
  type SuccessMsg = String
  type Environment = ExpenseService
    with PersonService
    with Console
    with Controller
  val liveEnv: Environment = new LiveExpenseService
    with LivePersonService
    with LiveConsole
    with LiveController
  type AppOp[A] = ReaderT[St, Environment, A] // stands for Kleisli(Environment => St[A])
  def readEnv: AppOp[Environment] = ReaderT.ask[St, Environment]

  def main(args: Array[SuccessMsg]): Unit = {
    val runVal: Either[Error, (AppState, Environment)] = readEnv.run(liveEnv).run(AppState.empty).value.run
    val tuple: (AppState, Environment) = runVal.getOrElse((AppState.empty, liveEnv))
    println(tuple._2.console.printLine("hello world").run)
  }
}
