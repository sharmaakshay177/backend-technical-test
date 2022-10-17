package addisonbackend.models

import zio.json.{DeriveJsonCodec, JsonCodec}

final case class Credentials(username: String, password: String)

object Credentials {
  implicit val codec: JsonCodec[Credentials] =
    DeriveJsonCodec.gen[Credentials]
}