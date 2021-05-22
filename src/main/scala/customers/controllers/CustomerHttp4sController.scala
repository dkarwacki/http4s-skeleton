package customers.controllers

import cats.effect._
import customers.models.Customer
import customers.services.CustomerService
import customers.CustomerId
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

class CustomerHttp4sController(customerService: CustomerService) {
  val routes = HttpRoutes
    .of[IO] {
      case GET -> Root / "customers" =>
        customerService.all flatMap (Ok(_))

      case GET -> Root / "customers" / LongVar(customerId) =>
        customerService.byId(CustomerId(customerId)) flatMap {
          case Some(customer) => Ok(customer)
          case None           => BadRequest(s"Cannot find customer with id: $customerId")
        }

      case req @ POST -> Root / "customers" =>
        for {
          customer <- req.as[Customer]
          response <- customerService.add(customer) flatMap (Ok(_))
        } yield response

      case req @ PUT -> Root / "customers" =>
        for {
          customer <- req.as[Customer]
          response <- customerService.update(customer) flatMap (Ok(_))
        } yield response

      case DELETE -> Root / "customers" / LongVar(customerId) =>
        customerService.delete(CustomerId(customerId)) flatMap (Ok(_))
    }
    .orNotFound
}
