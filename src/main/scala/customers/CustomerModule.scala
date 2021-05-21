package customers

import cats.effect.{ContextShift, IO, Timer}
import customers.controllers.CustomerController
import customers.services.CustomerServiceImpl

class CustomerModule(implicit cs: ContextShift[IO], timer: Timer[IO]) {
  lazy val service = new CustomerServiceImpl()
  lazy val controller = new CustomerController(service)
}
