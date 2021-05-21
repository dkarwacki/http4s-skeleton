package utils

import customers.CustomerId
import customers.models.Customer
import org.scalacheck.Gen
import org.scalacheck.rng.Seed

import java.time._
import java.time.temporal.ChronoUnit
import scala.jdk.CollectionConverters.CollectionHasAsScala

trait Generators extends DateTimeGenerators {

  val customerIdGen: Gen[CustomerId] = Gen.posNum[Long].map(CustomerId.apply)

  val customerGen: Gen[Customer] = for {
    id <- customerIdGen
    firstName <- Gen.alphaStr
    lastName <- Gen.alphaStr
    created <- localDateTimeGen
    modified <- Gen.option(localDateTimeGen.suchThat(_.isAfter(created)))
  } yield Customer(id, firstName, lastName, created, modified)

  implicit class GenOpt[T](gen: Gen[T]) {
    def one(): T = gen.pureApply(Gen.Parameters.default, Seed.random(), 1000)
    def take(n: Int): List[T] = List.fill(n)(one())
  }
}

trait DateTimeGenerators {

  implicit class LocalDateTimeOpt(localDateTime: LocalDateTime) {
    def roundedToSeconds(): Instant =
      localDateTime.toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
  }

  val zonedDateTimeGen: Gen[ZonedDateTime] =
    for {
      year <- Gen.choose(0, 3000)
      month <- Gen.choose(1, 12)
      maxDaysInMonth = Month.of(month).length(Year.of(year).isLeap)
      dayOfMonth <- Gen.choose(1, maxDaysInMonth)
      hour <- Gen.choose(0, 23)
      minute <- Gen.choose(0, 59)
      second <- Gen.choose(0, 59)
      nanoOfSecond <- Gen.choose(0, 999999999)
      zoneId <-
        Gen.oneOf(ZoneId.getAvailableZoneIds.asScala.toList).map(ZoneId.of)
    } yield ZonedDateTime.of(
      year,
      month,
      dayOfMonth,
      hour,
      minute,
      second,
      nanoOfSecond,
      zoneId
    )

  val offsetDateTimeGen: Gen[OffsetDateTime] = for {
    zonedDateTime <- zonedDateTimeGen
  } yield zonedDateTime.toOffsetDateTime

  val instantGen: Gen[Instant] = for {
    zonedDateTime <- zonedDateTimeGen
  } yield zonedDateTime.toInstant

  val localDateTimeGen: Gen[LocalDateTime] = for {
    zonedDateTime <- zonedDateTimeGen
  } yield zonedDateTime.toLocalDateTime
}
