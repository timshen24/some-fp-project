package fpfinal.model

import cats._
import cats.data._
import cats.implicits._
import fpfinal.app.Configuration.IsValid
import fpfinal.common.Validations._

import scala.collection.immutable.SortedSet

/**
 * Represents a single expense.
 *
 * The participants are the people among who this expense will be divided.
 * Note that this set does not contain the payer.
 *
 * For example, assume Alice buys a pizza for 10 dollars to eat with Bob and Charly.
 * In this case the expense information is as follows:
 *
 * - The payer is Alice
 * - The amount paid is $10.00
 * - The partipants are Bob and Charly
 *
 * @param payer the person who paid
 * @param amount the amount paid
 * @param participants the people involved in the expense (excluding the payer)
 */
class Expense private (
    val payer: Person,
    val amount: Money,
    val participants: NonEmptySet[Person]
) {

  /**
    * TODO #8: Divide this amount across the payer and the participants so each
    * has the same debt for this expense.
    *
    * For simplicity we don't care about losing cents. For example, dividing 1 dollar
    * among 3 participants should yield 33 cents of debt for each participant.
    */
  def amountByParticipant: Money = amount.divideBy(participants.length + 1).getOrElse(Money.zero)
}

object Expense {
  /**
   * Creates an instance of a Expense without performing validations.
   * Should only be used in tests.
   */
  def unsafeCreate(
      payer: Person,
      amount: Money,
      participants: List[Person]
  ): Expense =
    new Expense(
      payer,
      amount,
      NonEmptySet.fromSetUnsafe(SortedSet.from(participants))
    )

  /**
    * TODO #9: Create a validated expense. The validations to perform are:
    * - The participants list should not be empty
    * - The payer should not be included in the participants
    *
    * Note: List's contains method will use == equality. Figure out a way
    * to use the equality instance received in the implicit argument eqPerson
    */
  def create(
      payer: Person,
      amount: Money,
      participants: List[Person]
  )(implicit eqPerson: Eq[Person]): IsValid[Expense] = {
    (nonEmptySet(participants),
      Validated.condNec(
        Foldable[List].forall(participants)(_.neqv(payer)),
        payer, "payer cannot be included in participants"
      )
      ).mapN((ps, p) => new Expense(p, amount, ps))
  }

  /**
    * TODO #9: Implement an Eq instance by comparing every field,
    * using the corresponding Eq instance for each type
    * (i.e.: Person, Money, NonEmptySet[Person]).
    */
  implicit def eqExpense(implicit eqPerson: Eq[Person], eqMoney: Eq[Money]): Eq[Expense] =
    Eq.instance(
      (ex1, ex2) => ex1.payer === ex2.payer && ex1.amount === ex2.amount && ex1.participants === ex2.participants
    )

  /**
    * TODO #10: Implement a Show instance with the following format:
    *
    * Expense[Payer=Martin,Amount=$10.00,Participants=Bob,Susan]
    */
  implicit val showExpense: Show[Expense] = Show.show(expense => s"Expense[Payer=${expense.payer},Amount=${expense.amount.show},Participants=${expense.participants.mkString_(",")}]")
}
