package fpfinal.app

import cats.data._
import fpfinal.common.IO
import fpfinal.service._

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
}
