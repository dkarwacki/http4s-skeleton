package customers

import cats.effect.{IO, Timer}
import cats.implicits.catsSyntaxOptionId
import customers.controllers.{CustomerHttp4sController, CustomerTapirController}
import customers.models.Customer
import customers.services.CustomerService
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import utils.Generators

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext

class CustomerControllerTest extends AnyWordSpec with Matchers {

  "CustomerController" should {
    "return response with status 200 and customers" in new CustomerControllerFixture {
      //given
      val request = Request[IO](Method.GET, Uri(path = "/customers"))

      //when
      val tapirResult = tapirController.routes.run(request).unsafeRunSync()
      val http4sResult = http4sController.routes.run(request).unsafeRunSync()

      //then
      tapirResult.status shouldEqual Status.Ok
      tapirResult.as[List[Customer]].unsafeRunSync() shouldEqual List(customer)

      http4sResult.status shouldEqual Status.Ok
      http4sResult.as[List[Customer]].unsafeRunSync() shouldEqual List(customer)
    }

    "return response with status 200 and customer found by id" in new CustomerControllerFixture {
      //given
      println(customer)
      val request =
        Request[IO](Method.GET, Uri(path = s"/customers/${customer.id}"))

      //when
      val tapirResult = tapirController.routes.run(request).unsafeRunSync()
      val http4sResult = http4sController.routes.run(request).unsafeRunSync()

      //then
      tapirResult.status shouldEqual Status.Ok
      tapirResult.as[Customer].unsafeRunSync() shouldEqual customer

      http4sResult.status shouldEqual Status.Ok
      http4sResult.as[Customer].unsafeRunSync() shouldEqual customer
    }

    "return response with status 400 if customer cannot be found by id" in new CustomerControllerFixture {
      //given
      when(customerService.byId(customer.id)).thenReturn(IO.pure(None))

      val request =
        Request[IO](Method.GET, Uri(path = s"/customers/${customer.id}"))

      //when
      val tapirResult = tapirController.routes.run(request).unsafeRunSync()
      val http4sResult = http4sController.routes.run(request).unsafeRunSync()

      //then
      tapirResult.status shouldEqual Status.BadRequest
      http4sResult.status shouldEqual Status.BadRequest
    }

    "return response with status 200 and number of customers deleted by id" in new CustomerControllerFixture {
      //given
      val request =
        Request[IO](Method.DELETE, Uri(path = s"/customers/${customer.id}"))

      //when
      val tapirResult = tapirController.routes.run(request).unsafeRunSync()
      val http4sResult = http4sController.routes.run(request).unsafeRunSync()

      //then
      tapirResult.status shouldEqual Status.Ok
      tapirResult.as[Int].unsafeRunSync() shouldEqual 1

      http4sResult.status shouldEqual Status.Ok
      http4sResult.as[Int].unsafeRunSync() shouldEqual 1
    }

    "return response with status 200 and created customer" in new CustomerControllerFixture {
      //given
      val request =
        Request[IO](Method.POST, Uri(path = s"/customers")).withEntity(customer)

      //when
      val tapirResult = tapirController.routes.run(request).unsafeRunSync()
      val http4sResult = http4sController.routes.run(request).unsafeRunSync()

      //then
      tapirResult.status shouldEqual Status.Ok
      tapirResult.as[Customer].unsafeRunSync() shouldEqual customer

      http4sResult.status shouldEqual Status.Ok
      http4sResult.as[Customer].unsafeRunSync() shouldEqual customer
    }

    "return response with status 200 and updated customer" in new CustomerControllerFixture {
      //given
      val request =
        Request[IO](Method.PUT, Uri(path = s"/customers")).withEntity(customer)

      //when
      val tapirResult = tapirController.routes.run(request).unsafeRunSync()
      val http4sResult = http4sController.routes.run(request).unsafeRunSync()

      //then
      tapirResult.status shouldEqual Status.Ok
      tapirResult.as[Customer].unsafeRunSync() shouldEqual customer

      http4sResult.status shouldEqual Status.Ok
      http4sResult.as[Customer].unsafeRunSync() shouldEqual customer
    }

    "return status 404 when calling not existing endpoint" in new CustomerControllerFixture {
      //given
      val request = Request[IO](Method.GET, Uri(path = s"/notExisting"))

      //when
      val tapirResult = tapirController.routes.run(request).unsafeRunSync()
      val http4sResult = http4sController.routes.run(request).unsafeRunSync()

      //then
      tapirResult.status shouldEqual Status.NotFound
      http4sResult.status shouldEqual Status.NotFound
    }
  }
}

trait CustomerControllerFixture extends MockitoSugar with Generators {
  implicit val timer = IO.timer(ExecutionContext.global)
  implicit val cs = IO.contextShift(ExecutionContext.global)

  val customer = customerGen.one()
  val now = LocalDateTime.of(2020, 10, 10, 10, 10, 10)

  val customerService = mock[CustomerService]
  when(customerService.add(customer)).thenReturn(IO.pure(customer))
  when(customerService.update(customer)).thenReturn(IO.pure(customer))
  when(customerService.delete(customer.id)).thenReturn(IO.pure(1))
  when(customerService.byId(customer.id)).thenReturn(IO.pure(customer.some))
  when(customerService.all).thenReturn(IO.pure(List(customer)))

  val tapirController = new CustomerTapirController(customerService)
  val http4sController = new CustomerHttp4sController(customerService)
}
