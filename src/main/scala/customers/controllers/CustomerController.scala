package customers.controllers

import cats.effect._
import customers.models.Customer
import customers.services.CustomerService
import customers.CustomerId
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

class CustomerController(customerService: CustomerService) {
  val routes = HttpRoutes
    .of[IO] {
      case GET -> Root / "customers" =>
        customerService.all flatMap (Ok(_))
      case GET -> Root / "customers" / LongVar(customerId) =>
        customerService.byId(CustomerId(customerId)) flatMap {
          case Some(customer) => Ok(customer)
          case None           => NotFound(s"Cannot find customer with id: $customerId")
        }
      case req @ POST -> Root / "customers" =>
        for {
          customer <- req.as[Customer]
          response <- customerService.add(customer) flatMap (Ok(_))
        } yield response
      case DELETE -> Root / "customers" / LongVar(customerId) =>
        customerService.delete(CustomerId(customerId)) flatMap (Ok(_))
    }
    .orNotFound
}
