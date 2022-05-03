package fpfinal.model

import cats._
import cats.implicits._

class DebtByPayee private (val debtByPayee: Map[Person, Money]) {

  /**
    * TODO: Return the debt for this payee
    */
  def debtForPayee(person: Person): Option[Money] = debtByPayee.get(person)

  /**
    * TODO: Return all the payees as a list
    */
  def allPayees(): List[Person] = debtByPayee.keys.toList
}

object DebtByPayee {
  def unsafeCreate(debtByPayee: Map[Person, Money]): DebtByPayee =
    new DebtByPayee(debtByPayee)

  /**
    * TODO: Create a DebtByPayee instance using the information from this Expense.
    * Each participant should get the same debt to the payer.
    */
  def fromExpense(expense: Expense): DebtByPayee = {
    val m: Map[Person, Money] = expense.participants.map(p => (p, expense.amountByParticipant)).foldLeft(Map[Person, Money]()) {
      case (b: Map[Person, Money], (p, m)) => b + (p -> m)
    }
    new DebtByPayee(m)
  }

  def singleton(person: Person, money: Money): DebtByPayee = new DebtByPayee(Map(person -> money))

  /**
    * TODO: Implement an eq instance and their corresponding tests.
    * Two values are equal iff their debtByPayee maps are equal.
    */
  implicit def eqDebtByPayee(implicit
      eqMap: Eq[Map[Person, Money]]
  ): Eq[DebtByPayee] = Eq.instance((d1, d2) => d1.debtByPayee === d2.debtByPayee)

  /**
    * TODO: Implement a monoid instance.
    *
    * Hint: Use the monoidMap instance and a suitable method to convert it
    * to the instance you need.
    */
  implicit def monoidDebtByPayee(implicit
      monoidMap: Monoid[Map[Person, Money]]
  ): Monoid[DebtByPayee] = new Monoid[DebtByPayee] {
    override def empty: DebtByPayee = new DebtByPayee(Map.empty[Person, Money])

    override def combine(x: DebtByPayee, y: DebtByPayee): DebtByPayee = new DebtByPayee(x.debtByPayee |+| y.debtByPayee)
  }

  implicit def showDebtByPayee(implicit
      personShow: Show[Person],
      moneyShow: Show[Money]
  ): Show[DebtByPayee] =
    Show.show { d =>
      d.allPayees()
        .foldMap(payee =>
          s"- ${payee.show}: ${d.debtForPayee(payee).getOrElse(Money.zero).show}\n"
        )
    }
}
