package content

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.{
  GlobalKTable,
  JoinWindows,
  TimeWindows,
  Windowed
}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream.{KGroupedStream, KStream, KTable}
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Properties
import scala.concurrent.duration._

object KafkaStreamsApp {

  implicit def serde[A >: Null: Decoder: Encoder]: Serde[A] = {
    val serializer = (a: A) => a.asJson.noSpaces.getBytes
    val deserializer = (aAsBytes: Array[Byte]) => {
      val aAsString = new String(aAsBytes)
      val aOrError = decode[A](aAsString)
      aOrError match {
        case Right(a) => Option(a)
        case Left(error) =>
          println(
            s"There was an error converting the message $aOrError, $error"
          )
          Option.empty
      }
    }
    Serdes.fromFn[A](serializer, deserializer)
  }

  // Topics
  final val OrdersByUserTopic = "orders-by-user"
  final val DiscountProfilesByUserTopic = "discount-profiles-by-user"
  final val DiscountsTopic = "discounts"
  final val OrdersTopic = "orders"
  final val PaymentsTopic = "payments"
  final val PayedOrdersTopic = "payed-orders"

  type UserId = String
  type Profile = String
  type Product = String
  type OrderId = String

  case class Order(
      orderId: OrderId,
      user: UserId,
      products: List[Product],
      amount: Double
  )

  // Discounts profiles are a (String, String) topic

  case class Discount(profile: Profile, amount: Double)

  case class Payment(orderId: OrderId, status: String)

  def main(args: Array[String]): Unit = {
    val builder = new StreamsBuilder

    // KStream definition
    val usersOrdersStream: KStream[UserId, Order] =
      builder.stream[UserId, Order](OrdersByUserTopic)

    // KTable
    val usersProfilesTable =
      builder.table[UserId, Profile](DiscountProfilesByUserTopic)

    // Global Ktable
    val discountProfileGTable =
      builder.globalTable[Profile, Discount](DiscountsTopic)

    // KStream Operations
    val expensiveOrders = usersOrdersStream.filter { (userId, order) =>
      order.amount > 1000
    }

    val listOfProducts = usersOrdersStream.mapValues { order =>
      order.products
    }

    // Extract each product from the list with it's coresponding id
    val productsStream: KStream[UserId, Product] =
      usersOrdersStream.flatMapValues(_.products)

    // join => done with KTable
    val ordersWithProfile = usersOrdersStream.join(usersProfilesTable) {
      (order, profile) => (order, profile)
    }

    val discountedOrdersStream: KStream[UserId, Order] =
      ordersWithProfile.join(discountProfileGTable)(
        { case (_, (_, profile)) => profile }, // Joining key
        { case ((order, _), discount) =>
          order.copy(amount = order.amount - discount.amount)
        }
      )

    // pick another identifier
    val orderStream: KStream[OrderId, Order] =
      discountedOrdersStream.selectKey((userId, order) => order.orderId)
    val paymentStream = builder.stream[OrderId, Payment](PaymentsTopic)

    val joinWindow = JoinWindows.of(Duration.of(5, ChronoUnit.MINUTES))

    val orderPaid = orderStream
      .join(paymentStream)(
        (
            (
                order,
                payment
            ) =>
              if (payment.status == "PAID") Option(order)
              else Option.empty[Order]
        ),
        joinWindow
      )
      .filter((orderId, maybeOrder) => maybeOrder.isDefined)
      .flatMapValues(maybeOrder => maybeOrder.toIterable)

    // sink
    orderPaid.to(PayedOrdersTopic)

    val topology = builder.build

    val props = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "orders-application")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass)

    // println(topology.describe)
    val application = new KafkaStreams(topology, props)
    application.start()
  }
}
