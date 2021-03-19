package fpfinal.model

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import fpfinal.Generators
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ExpenseSpec extends AnyFunSuite with Matchers with Generators {
  test("create a valid expense") {
    implicit val g: Arbitrary[(Person, List[Person])] = Arbitrary {
      for {
        payer <- implicitly[Arbitrary[Person]].arbitrary
        participants <- Gen.nonEmptyListOf(
          implicitly[Arbitrary[Person]].arbitrary.filter(_ != payer)
        )
      } yield (payer, participants)
    }

    forAll { (money: Money, p: (Person, List[Person])) =>
      Expense.create(p._1, money, p._2) === Valid(
        Expense.unsafeCreate(p._1, money, p._2)
      )
    }
  }

  test("create an invalid expense with no participants") {
    forAll { (money: Money, payer: Person) =>
      Expense.create(payer, money, List.empty[Person]).isInvalid
    }
  }

  test("create an invalid expense with payer included in participants") {
    forAll { (money: Money, payer: Person, participants: List[Person]) =>
      Expense.create(payer, money, payer :: participants).isInvalid
    }
  }
}
