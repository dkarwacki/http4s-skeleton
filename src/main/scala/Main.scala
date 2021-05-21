import cats.effect.{ExitCode, IO, IOApp}
import config.Configuration
import customers.CustomerModule
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val config = ConfigSource.default.loadOrThrow[Configuration]
    val customerModule = new CustomerModule

    val routes = customerModule.controller.routes

    BlazeServerBuilder
      .apply[IO](ExecutionContext.global)
      .bindHttp(config.http.port, config.http.host)
      .withHttpApp(routes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
