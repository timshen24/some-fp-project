package fpfinal.model

import cats._
import cats.implicits._

class DebtByPayer private (val debtByPerson: Map[Person, DebtByPayee]) {

  /**
    * TODO: Get the debt summary by payee for this payer
    */
  def debtForPayer(person: Person): Option[DebtByPayee] = debtByPerson.get(person)

  /**
    * TODO: Get all the payers in a list
    */
  def allPayers(): List[Person] = debtByPerson.keys.toList

  def simplified: DebtByPayer = {
    def payeesFor(person: Person): List[Person] =
      debtByPerson.get(person).toList.flatMap(_.allPayees())

    def owes(p1: Person, p2: Person): Money =
      debtByPerson.get(p2).flatMap(_.debtForPayee(p1)).getOrElse(Money.zero)

    def createDebt(p1: Person, p2: Person, p1OwesP2: Money, p2OwesP1: Money): Option[DebtByPayer] = {
      if(p2OwesP1 > p1OwesP2) {
        val debtByPayer = new DebtByPayer(Map(p1 -> DebtByPayee.singleton(p2, p2OwesP1 minus p1OwesP2)))
        Some(debtByPayer)
      } else None
    }

    val debtByPayes = for {
      p1 <- allPayers()
      p2 <- payeesFor(p1)
      p1OwesP2 = owes(p1, p2)
      p2OwesP1 = owes(p2, p1)
      d <- createDebt(p1, p2, p1OwesP2, p2OwesP1).toList
    } yield d

    debtByPayes.foldMap(identity)
  }
}

object DebtByPayer {
  def unsafeCreate(debtByPerson: Map[Person, DebtByPayee]): DebtByPayer =
    new DebtByPayer(debtByPerson)

  def fromExpense(expense: Expense): DebtByPayer =
    new DebtByPayer(Map(expense.payer -> DebtByPayee.fromExpense(expense)))

  implicit def eqDebtByPayer(implicit
      eqMap: Eq[Map[Person, DebtByPayee]]
  ): Eq[DebtByPayer] =
    Eq.instance((d1, d2) => d1.debtByPerson === d2.debtByPerson)

  /**
    * TODO: Implement a monoid instance.
    *
    * Hint: Use the monoidMap instance and a suitable method to convert it
    * to the instance you need.
    */
  implicit def monoidDebtByPayer(implicit
      monoidMap: Monoid[Map[Person, DebtByPayee]]
  ): Monoid[DebtByPayer] = new Monoid[DebtByPayer] {
    override def empty: DebtByPayer = new DebtByPayer(Map.empty[Person, DebtByPayee])

    override def combine(x: DebtByPayer, y: DebtByPayer): DebtByPayer = new DebtByPayer(x.debtByPerson |+| y.debtByPerson)
  }

  implicit def showDebtByPayer(implicit
      showPerson: Show[Person],
      showDebtByPayee: Show[DebtByPayee]
  ): Show[DebtByPayer] =
    Show.show { debtByPayer =>
      s"""Debt by payer:
       |
       |${debtByPayer
        .allPayers()
        .toNel
        .fold("  No debts found")(
          _.foldMap(payer =>
            s"${payer.show}:\n" + debtByPayer
              .debtForPayer(payer)
              .foldMap(_.show)
          )
        )}""".stripMargin
    }
}
