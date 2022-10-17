package addisonbackend.services

import addisonbackend.models.{Credentials, User, UserToken}
import addisonbackend.server.AppError.{AuthenticationError, GenericError}
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils
import zio.{Random, Task, ULayer, ZIO, ZLayer}

import scala.concurrent.{ExecutionContext, Future}

trait AsyncTokenService extends TokenService {
  def authenticate(credentials: Credentials): Task[User]
  def issueToken(user: User): Task[UserToken]
  def requestToken(credentials: Credentials): Task[UserToken] =
    for {
      user  <- authenticate(credentials)
      token <- issueToken(user)
    } yield token
}

final case class AsyncTokenServiceLive() extends AsyncTokenService {

  implicit val globalExecutionContext: ExecutionContext = ExecutionContext.global

  // these methods exists to simulate some Future computation
  def generateUserFromFuture(randomTimeToSleep: Long, credentials: Credentials)(implicit
      executionContext: ExecutionContext
  ): Future[User] = {
    Thread.sleep(randomTimeToSleep)
    if (credentials.username.equalsIgnoreCase(credentials.password) && StringUtils.isAllUpperCase(credentials.password))
      Future.successful(User.make(credentials.username))
    else Future.failed(AuthenticationError("AuthenticationError: Credentials are not correct"))
  }

  def generateUserTokenFromFuture(randomTimeToSleep: Long, user: User)(implicit
      executionContext: ExecutionContext
  ): Future[UserToken] = {
    Thread.sleep(randomTimeToSleep)
    if (user.userId.head.equals('A')) Future.successful(UserToken.getToken(user))
    else Future.failed(GenericError("User Id validation failed, reason it starts with A"))
  }

  override def authenticate(credentials: Credentials): Task[User] =
    for {
      randomTime <- Random.nextLongBetween(0, 5001)
      _          <- ZIO.log(s"Random Time to sleep :$randomTime")
      user <- ZIO
                .fromFuture(ec => generateUserFromFuture(randomTime, credentials)(ec))
    } yield user

  override def issueToken(user: User): Task[UserToken] =
    for {
      randomTime <- Random.nextLongBetween(0, 5001)
      _          <- ZIO.log(s"Random Time to sleep :$randomTime")
      token <- ZIO
                 .fromFuture(ec => generateUserTokenFromFuture(randomTime, user)(ec))
    } yield token
}

object AsyncTokenServiceLive {
  val layer: ULayer[AsyncTokenService] =
    ZLayer.fromFunction(AsyncTokenServiceLive.apply _)
}
