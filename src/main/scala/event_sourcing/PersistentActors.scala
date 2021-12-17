package event_sourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

import java.util.Date

object PersistentActors extends App {

  /*
   * Scenario: We have a business and an accountant which keeps track of our invoices.
   */

  // COMMANDS
  case class Invoice(recipient: String, date: Date, amount: Int)

  // Event
  case class InvoiceRecorded(id: Int, recipient: String, date: Date, amount: Int)

  class Accountant extends PersistentActor with ActorLogging {

    var latestInvoiceId = 0
    var totalAmount = 0

    // should be unique for each actor
    override def persistenceId: String = "simple-accountant"

    // normal receive method
    override def receiveCommand: Receive = {
      case Invoice(recipient, date, amount) =>
        /*
         * Pattern:
         *   When you receive a command:
         *    1) you create an event to persist into a store
         *    2) you persist the event, the pass in a callback that will get triggered once the event is written
         *    3) update the actor when the event is persisted
         */

        log.info(s"Received invoice for amount: $amount")
        persist(InvoiceRecorded(latestInvoiceId, recipient, date, amount)) {e =>
          latestInvoiceId += 1
          totalAmount += amount
          log.info(s"Persisted: $e, as invoice #${e.id}, for total amount $totalAmount")
        }
    }

    // handler that will be called on recovery
    override def receiveRecover: Receive = {
      /*
       * Best practice: Follow the logic from the persist steps of the receiveCommand
       */
      case InvoiceRecorded(id, _, _, amount) =>
        latestInvoiceId += id
        totalAmount += amount
    }
  }

  val system: ActorSystem = ActorSystem("PersistenceActorSystem")
  val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

  for (i <- 1 to 10) {
    accountant ! Invoice("The Sofa Company", new Date, i * 1000)
  }
}
