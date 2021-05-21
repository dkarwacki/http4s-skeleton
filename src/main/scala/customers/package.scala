import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype

package object customers {
  @newtype case class CustomerId(value: Long)

  object CustomerId {
    implicit val decoder: Decoder[CustomerId] =
      Decoder.decodeLong.map(CustomerId.apply)
    implicit val encoder: Encoder[CustomerId] =
      Encoder.encodeLong.contramap(_.value)
  }
}
