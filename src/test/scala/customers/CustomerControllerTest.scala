package customers

import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import customers.controllers.CustomerController
import customers.models.Customer
import customers.services.CustomerService
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import utils.Generators

import java.time.LocalDateTime

class CustomerControllerTest extends AnyWordSpec with Matchers {

  "CustomerController" should {
    "return response with status 200 and customers" in new CustomerControllerFixture {
      //given
      val request = Request[IO](Method.GET, Uri(path = "/customers"))

      //when
      val result = controller.routes.run(request).unsafeRunSync()

      //then
      result.status shouldEqual Status.Ok
      result.as[List[Customer]].unsafeRunSync() shouldEqual List(customer)
    }

    "return response with status 200 and customer found by id" in new CustomerControllerFixture {
      //given
      val request =
        Request[IO](Method.GET, Uri(path = s"/customers/${customer.id}"))

      //when
      val result = controller.routes.run(request).unsafeRunSync()

      //then
      result.status shouldEqual Status.Ok
      result.as[Customer].unsafeRunSync() shouldEqual customer
    }

    "return response with status 404 if customer cannot be found by id" in new CustomerControllerFixture {
      //given
      val notExistingId: Long = customer.id.value + 1

      val request =
        Request[IO](Method.GET, Uri(path = s"/customers/$notExistingId"))

      //when
      val result = controller.routes.run(request).unsafeRunSync()

      //then
      result.status shouldEqual Status.NotFound
    }

    "return response with status 200 and number of customers deleted by id" in new CustomerControllerFixture {
      //given
      val request =
        Request[IO](Method.DELETE, Uri(path = s"/customers/${customer.id}"))

      //when
      val result = controller.routes.run(request).unsafeRunSync()

      //then
      result.status shouldEqual Status.Ok
      result.as[Int].unsafeRunSync() shouldEqual 1
    }

    "return response with status 200 and created customer" in new CustomerControllerFixture {
      //given
      val request =
        Request[IO](Method.POST, Uri(path = s"/customers")).withEntity(customer)

      //when
      val result = controller.routes.run(request).unsafeRunSync()

      //then
      result.status shouldEqual Status.Ok
      result.as[Customer].unsafeRunSync() shouldEqual customer
    }

    "return response with status 200 and updated customer" in new CustomerControllerFixture {
      //given
      val request =
        Request[IO](Method.PUT, Uri(path = s"/customers")).withEntity(customer)

      //when
      val result = controller.routes.run(request).unsafeRunSync()

      //then
      result.status shouldEqual Status.Ok
      result.as[Customer].unsafeRunSync() shouldEqual customer
    }

    "return status 404 when calling not existing endpoint" in new CustomerControllerFixture {
      //given
      val request = Request[IO](Method.GET, Uri(path = s"/notExisting"))

      //when
      val result = controller.routes.run(request).unsafeRunSync()

      //then
      result.status shouldEqual Status.NotFound
    }
  }
}

trait CustomerControllerFixture extends MockitoSugar with Generators {

  val customer = customerGen.one()
  val now = LocalDateTime.of(2020, 10, 10, 10, 10, 10)

  private val customerService = mock[CustomerService]
  when(customerService.add(customer)).thenReturn(IO.pure(customer))
  when(customerService.delete(customer.id)).thenReturn(IO.pure(1))
  when(customerService.byId(customer.id)).thenReturn(IO.pure(customer.some))
  when(customerService.byId(any())).thenReturn(IO.pure(None))
  when(customerService.all).thenReturn(IO.pure(List(customer)))

  val controller = new CustomerController(customerService)
}
