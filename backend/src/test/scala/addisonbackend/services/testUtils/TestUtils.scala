package addisonbackend.services.testUtils

import zio.{Exit, Unsafe}
import zio._

object TestUtils {
  /**
   * Not a correct way, but a way to test !! :(
   */

  // doing unsafeRun to test failure cases
  def unsafeRun(block: => Exit[_, _]): Exit[_ , _] = {
    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run(block)
    }
  }

}
