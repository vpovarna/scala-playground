package content

import scala.concurrent.Future
import cats.instances.char

object MonadsForBeginners {

  case class SafeValue[+T](private val internalValue: T) { // "constructor" = pure or unit
    def get: T = synchronized {
      // does something interesting
      internalValue
    }

    // transformer is a transformation function.
    def flatMap[S](
        transformer: T => SafeValue[S]
    ): SafeValue[S] = // bind or flatMap
      synchronized {
        transformer(internalValue)
      }
  }

  // "external" API
  def gimmeSafeValue[T](value: T): SafeValue[T] = SafeValue(value)

  val safeString: SafeValue[String] = gimmeSafeValue("Scala is awesome")
  // Extract
  val string = safeString.get
  // Transform
  val upperString = string.toUpperCase()
  // Wrap
  val upperSafeString = SafeValue(upperString)

  // Extract & Transform & Wrap = ETW pattern
  // Compressed:
  val upperSafeString2 = safeString.flatMap(s => SafeValue(s.toUpperCase))

  // Examples 1: census
  case class Person(firstName: String, lastName: String) {
    assert(firstName != null && lastName != null)
  }

  // census API for census example
  def getPerson(firstName: String, lastName: String): Person = {
    if (firstName != null) {
      if (lastName != null) {
        Person(firstName, lastName)
      } else {
        null
      }
    } else {
      null
    }
  }

  def getPersonBetter(firstName: String, lastName: String): Option[Person] = {
    Option(firstName).flatMap { fName =>
      Option(lastName).flatMap { lName =>
        Option(Person(fName, lName))
      }
    }
  }

  def getPersonWithForComprehensive(
      firstName: String,
      lastName: String
  ): Option[Person] = for {
    fName <- Option(firstName)
    lName <- Option(lastName)
  } yield Person(fName, lName)

  import scala.concurrent.ExecutionContext.Implicits.global

  // Example 2: async fetches
  case class User(id: String)
  case class Product(sku: String, price: Double)

  def getUser(url: String): Future[User] = Future {
    User("Daniel")
  }

  def getLastOrder(userId: String): Future[Product] = Future {
    Product("1234-23121", 99.99)
  }

  val danielsUrl = "my.store.com/users/daniel"

  val product: Future[Double] = getUser(danielsUrl)
    .flatMap(user => getLastOrder(user.id))
    .map(_.price * 1.19)

  // using for comprehensive
  val productWithVAT = for {
    user <- getUser(danielsUrl)
    product <- getLastOrder(user.id)
  } yield product.price * 1.19

  // Example3: double for loops
  val numbers = List(1, 2, 3)
  val chars = List('a', 'b', 'c')

  val checkerBoard =
    numbers.flatMap(number => chars.map(char => (number, char)))
  val checkerBoard2 = for {
    number <- numbers
    char <- chars
  } yield (number, char)

  // Properties

  // Prop1
  def twoConsecutive(x: Int) = List(x, x + 1)
  twoConsecutive(3)

  List(3).flatMap(twoConsecutive) //List(3,4)
  // Monad(x).flatMap(f) == f(x)

  // Prop2
  List(1, 2, 3).flatMap(x => List(x)) // List(1,2,3)
  // Monad(x).flatMap(x => Monad(x)) // Useless -> returns Monad(x)

  // Prop3 -> ETW -> ETW
  val incrementer = (x: Int) => List(x, x + 1)
  val doubler = (x: Int) => List(x, x * 2)

  def main(args: Array[String]): Unit = {
    println(numbers.flatMap(incrementer).flatMap(doubler))
  }

  // Monad(v).flatMap(f).flatMap(g) == Monad(v).flatMap(x => f(x).flatMap(g))



}
