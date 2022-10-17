package addisonbackend.services

import addisonbackend.models.{Credentials, User, UserToken}
import addisonbackend.server.AppError.{AuthenticationError, GenericError}
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils
import zio.macros._
import zio.{IO, Random, Task, ULayer, ZIO, ZLayer}

@accessible
trait SyncTokenService extends TokenService {
  def authenticate(credentials: Credentials): IO[AuthenticationError, User]
  def issueToken(user: User): IO[GenericError, UserToken]
  def requestToken(credentials: Credentials): Task[UserToken] =
    for {
      user  <- authenticate(credentials)
      token <- issueToken(user)
    } yield token
}

final case class SyncTokenServiceLive() extends SyncTokenService {
  override def authenticate(credentials: Credentials): IO[AuthenticationError, User] =
    for {
      user <- ZIO.fromEither[AuthenticationError, User] {
                val isValid = credentials.username.equalsIgnoreCase(credentials.password) && StringUtils
                  .isAllUpperCase(
                    credentials.password
                  )
                if (isValid) Right(User.make(credentials.username))
                else Left(AuthenticationError("AuthenticationError: Credentials are not correct"))
              }

      randomTime <- Random.nextLongBetween(0, 5001)
      _          <- ZIO.log(s"Random Time to sleep :$randomTime")
    } yield user

  override def issueToken(user: User): IO[GenericError, UserToken] =
    for {
      userToken <- ZIO.fromEither(
                     if (user.userId.head.equals('A'))
                       Left(GenericError("User Id validation failed, reason it starts with A"))
                     else Right(UserToken.getToken(user))
                   )
      randomTime <- Random.nextLongBetween(0, 5001)
      _          <- ZIO.log(s"Random Time to sleep :$randomTime")
    } yield userToken
}

object SyncTokenServiceLive {
  val layer: ULayer[SyncTokenService] =
    ZLayer.fromFunction(SyncTokenServiceLive.apply _)
}
