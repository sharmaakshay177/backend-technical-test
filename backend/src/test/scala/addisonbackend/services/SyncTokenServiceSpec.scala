package addisonbackend.services

import addisonbackend.models.{Credentials, User, UserToken}
import addisonbackend.server.AppError
import addisonbackend.server.AppError.{AuthenticationError, GenericError}
import zio._
import zio.mock._
import zio.test._
import zio.ZLayer._

import java.time.LocalDateTime

object SyncTokenServiceSpec extends ZIOSpecDefault {

  private val service = SyncTokenServiceLive()

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Sync Token service should")(
      suite("handler authentication cases")(
        test("authenticate if correct credentials are passed") {
          for {
            credentials <- ZIO.attempt(Credentials("house", "HOUSE"))
            user <- service.authenticate(credentials)
          } yield assertTrue(user == User("house"))
        },
        test("raise AuthenticationError for incorrect credentials") {
          val credentials = Credentials("house", "House")
          val effect = Unsafe.unsafe { implicit unsafe =>
            Runtime.default.unsafe.run(
              service.authenticate(credentials)
            )
          }
          assertTrue(effect.isFailure)
        }
      ),
      suite("handler issueToken cases")(
        test("correct user name") {
          val user = User("house")
          val timeNow = LocalDateTime.now().toString
          val expectedResult =
            UserToken(s"${user.userId}_$timeNow")

          // test conditions
          val sut = SyncTokenService.issueToken(user)
          val mockEnv =
            MockSyncTokenService.IssueToken(
              Assertion.equalTo(user),
              Expectation.value(expectedResult)
            )

          // actual test
          for {
            token <- sut.provideLayer(mockEnv)
          } yield assertTrue(token == expectedResult)
        },
      )

    )
}

object MockSyncTokenService extends Mock[SyncTokenService] {

  object Authenticate extends Effect[Credentials, AuthenticationError, User]
  object IssueToken   extends Effect[User, GenericError, UserToken]

  override val compose: URLayer[Proxy, SyncTokenService] =
    ZLayer.fromZIO {
      ZIO
        .service[mock.Proxy]
        .map { proxy =>
          new SyncTokenService {
            override def authenticate(credentials: Credentials): IO[AppError.AuthenticationError, User] =
              proxy(Authenticate, credentials)

            override def issueToken(user: User): IO[GenericError, UserToken] =
              proxy(IssueToken, user)
          }
        }
    }
}
