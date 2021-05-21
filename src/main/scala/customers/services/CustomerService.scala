package customers.services

import cats.effect.IO
import customers.models.Customer
import customers.CustomerId

import java.time.LocalDateTime

trait CustomerService {
  def add(customer: Customer): IO[Customer]
  def update(customer: Customer): IO[Customer]
  def delete(id: CustomerId): IO[Int]
  def byId(id: CustomerId): IO[Option[Customer]]
  def all: IO[List[Customer]]
}

class CustomerServiceImpl extends CustomerService {
  val customers = List(
    Customer(CustomerId(1), "John", "Doe", LocalDateTime.now()),
    Customer(CustomerId(2), "Amy", "Oro", LocalDateTime.now())
  )

  def add(customer: Customer): IO[Customer] = IO.pure { customer }
  def update(customer: Customer): IO[Customer] = IO.pure { customer }
  def delete(id: CustomerId): IO[Int] = IO.pure { 1 }
  def byId(id: CustomerId): IO[Option[Customer]] =
    IO.pure { customers.find(_.id == id) }
  def all: IO[List[Customer]] =
    IO.pure {
      customers
    }
}
