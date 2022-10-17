package addisonbackend.services

import addisonbackend.models.{Credentials, UserToken}
import zio.{Task, URLayer, ZLayer}

trait SimpleAsyncTokenService extends TokenService {
  def requestToken(credentials: Credentials): Task[UserToken]
}

final case class SimpleAsyncTokenServiceLive(tokenService: AsyncTokenService) extends SimpleAsyncTokenService {
  override def requestToken(credentials: Credentials): Task[UserToken] =
    tokenService.requestToken(credentials)
}

object SimpleAsyncTokenServiceLive {
  val layer: URLayer[AsyncTokenService, SimpleAsyncTokenService] =
    ZLayer.fromFunction(SimpleAsyncTokenServiceLive.apply _)
}
