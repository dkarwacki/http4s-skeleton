package customers.models

import cats.effect.IO
import customers.CustomerId
import io.circe.generic.auto._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._

import java.time.LocalDateTime

case class Customer(
    id: CustomerId,
    firstName: String,
    lastName: String,
    created: LocalDateTime,
    modified: Option[LocalDateTime] = None
)

object Customer {
  implicit val encoderList: EntityEncoder[IO, List[Customer]] =
    jsonEncoderOf[IO, List[Customer]]
  implicit val decoderList: EntityDecoder[IO, List[Customer]] =
    jsonOf[IO, List[Customer]]
  implicit val encoder: EntityEncoder[IO, Customer] =
    jsonEncoderOf[IO, Customer]
  implicit val decoder: EntityDecoder[IO, Customer] = jsonOf[IO, Customer]
}
