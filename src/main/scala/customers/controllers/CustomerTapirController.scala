package customers.controllers

import cats.effect._
import cats.implicits.{catsSyntaxEitherId, catsSyntaxOptionId, toSemigroupKOps}
import customers.models.Customer
import customers.services.CustomerService
import customers.CustomerId
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import io.circe.generic.auto._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import sttp.tapir.codec.newtype._
import sttp.tapir.server.ServerEndpoint

class CustomerTapirController(customerService: CustomerService)(implicit
    timer: Timer[IO],
    cs: ContextShift[IO]
) {

  private val all: ServerEndpoint[Unit, Unit, List[Customer], Any, IO] =
    endpoint.get
      .in("customers")
      .out(jsonBody[List[Customer]])
      .serverLogic(_ => customerService.all.map(_.asRight[Unit]))

  private val byId
      : ServerEndpoint[CustomerId, Unit, Option[Customer], Any, IO] =
    endpoint.get
      .in("customers" / path[CustomerId])
      .out(jsonBody[Option[Customer]])
      .serverLogic(id =>
        customerService.byId(id).map {
          case Some(customer) => customer.some.asRight
          case None           => ().asLeft
        }
      )

  private val create: ServerEndpoint[Customer, Unit, Customer, Any, IO] =
    endpoint.post
      .in("customers")
      .in(jsonBody[Customer])
      .out(jsonBody[Customer])
      .serverLogic(customer =>
        customerService.add(customer).map(_.asRight[Unit])
      )

  private val update: ServerEndpoint[Customer, Unit, Customer, Any, IO] =
    endpoint.put
      .in("customers")
      .in(jsonBody[Customer])
      .out(jsonBody[Customer])
      .serverLogic(customer =>
        customerService.update(customer).map(_.asRight[Unit])
      )

  private val delete: ServerEndpoint[CustomerId, Unit, Int, Any, IO] =
    endpoint.delete
      .in("customers" / path[CustomerId])
      .out(jsonBody[Int])
      .serverLogic(id => customerService.delete(id).map(_.asRight[Unit]))

  private val customerServerEndpoints = List(all, byId, create, update, delete)
  private val customerRoutes =
    Http4sServerInterpreter.toRoutes(customerServerEndpoints)
  private val swaggerRoutes = new SwaggerHttp4s(
    OpenAPIDocsInterpreter
      .toOpenAPI(customerServerEndpoints.map(_.endpoint), "customers", "1.0")
      .toYaml
  ).routes

  val routes = Router(
    "/" -> (customerRoutes <+> swaggerRoutes)
  ).orNotFound
}
