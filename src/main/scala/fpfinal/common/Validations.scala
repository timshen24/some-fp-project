package fpfinal.common

import cats.Order
import cats.data.{NonEmptyChain, NonEmptySet, Validated}
import fpfinal.app.Configuration.IsValid
import cats.implicits._

import scala.collection.immutable.SortedSet
import scala.util.{Failure, Success, Try}

object Validations {

  /**
    * TODO: Check that this String's length does not exceed the provided limit.
    */
  def maxLength(s: String, n: Int): IsValid[String] =
    Validated.condNec(s.length <= n, s, s"String's length must not exceed $n")

  /**
    * TODO: Turn this String into a validated double
    */
  def double(s: String): IsValid[Double] =
    Try(s.toDouble) match {
      case Failure(exception) => exception.getMessage.invalidNec[Double]
      case Success(value) => value.validNec[String]
    }

  def nonNegative(x: Double): IsValid[Double] =
    Validated.condNec(x >= 0, x, s"Double should be non-negative")

  def nonEmptySet[A: Order](list: List[A]): IsValid[NonEmptySet[A]] =
    Validated.fromOption(
      NonEmptySet.fromSet(SortedSet.from(list)(Order[A].toOrdering)),
      NonEmptyChain("List should be non-empty")
    )

  def nonEmptyString(s: String): IsValid[String] =
    Validated.condNec(s.nonEmpty, s, "String should be non-empty")

  def allLetters(s: String): IsValid[String] =
    Validated.condNec(s.forall(_.isLetter), s, "String should be all letters")
}
