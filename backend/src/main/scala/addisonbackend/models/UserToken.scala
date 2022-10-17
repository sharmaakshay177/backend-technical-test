package addisonbackend.models

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.LocalDateTime

final case class UserToken(token: String)

object UserToken {

  def getToken(user: User): UserToken =
    UserToken(s"${user.userId}_${LocalDateTime.now().toString}")


  implicit val codec: JsonCodec[UserToken] =
    DeriveJsonCodec.gen[UserToken]
}
