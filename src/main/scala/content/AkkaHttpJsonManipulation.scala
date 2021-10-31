package content

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import java.util.UUID
import java.time.Instant
import akka.event.slf4j.Logger
import akka.http.scaladsl.server.MediaTypeNegotiator
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

case class Person(name: String, age: Int)
case class UserAdded(id: String, timestamp: Long)

trait PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat2(Person)
  implicit val userFormat = jsonFormat2(UserAdded)
}

object AkkaHttpJsonManipulation
    extends PersonJsonProtocol
    with SprayJsonSupport {

  implicit val system: ActorSystem = ActorSystem("DemoAkkaApp")

  val route: Route =
    (path("api" / "user") & post) {
      entity(as[Person]) { person =>
        complete(
          UserAdded(
            UUID.randomUUID().toString(),
            Instant.now().getEpochSecond()
          )
        )
      }
    }

  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost", 8081).bind(route)
  }
}

object AkkaHttpCirce extends FailFastCirceSupport {

  import io.circe.generic.auto._ // implicit decoders / decoders

  implicit val system: ActorSystem = ActorSystem("DemoAkkaApp")

  val route: Route =
    (path("api" / "user") & post) {
      entity(as[Person]) { person =>
        complete(
          UserAdded(
            UUID.randomUUID().toString(),
            Instant.now().getEpochSecond()
          )
        )
      }
    }

  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost", 8082).bind(route)
  }
}
