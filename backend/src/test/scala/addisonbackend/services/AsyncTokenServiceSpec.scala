package addisonbackend.services

import addisonbackend.models.{Credentials, User}
import zio.Scope
import zio._
import zio.test._
import zio.test.{Spec, TestEnvironment, TestRandom, ZIOSpecDefault}

object AsyncTokenServiceSpec extends ZIOSpecDefault {

  private val service: AsyncTokenServiceLive = AsyncTokenServiceLive()

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("AsyncTokenService should") {
      test("return user if correct credentials are passed") {
        for {
          _         <- TestRandom.setSeed(1L)
          credential = Credentials("house", "HOUSE")
          user      <- service.authenticate(credential)
        } yield assertTrue(user == User("house"))
      }
    }

}
