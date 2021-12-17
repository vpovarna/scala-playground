package content

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

object Routers extends App {

  /*
   * Method 1: Manual router
   */
  class Master extends Actor with ActorLogging {
    // Step 1 - crete routes
    private val slaves = for(i <- 1 to 5) yield  {
      val slave = context.actorOf(Props[Slave], s"slave_$i")
      context.watch(slave)
      ActorRefRoutee(slave)
    }
    // Step 2 - create router
    private val router: Router = Router(RoundRobinRoutingLogic(), slaves)

    override def receive: Receive = {
      // Step 4 -> handle the termination / lifecycle of the routes
      case Terminated(ref) =>
        router.removeRoutee(ref)
        val newSlave = context.actorOf(Props[Slave])
        context.watch(newSlave)
        router.addRoutee(newSlave)

      // Step 3 route the messages
      case message =>
        log.info(s"Received message: $message")
        router.route(message, sender())

    }
  }

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system: ActorSystem = ActorSystem("RoutersDemo" /*TODO*/)
  val master = system.actorOf(Props[Master], "MasterActor")

  for (i <- 1 to 10) {
    master ! s"[$i] Hello from the world"
  }

}
