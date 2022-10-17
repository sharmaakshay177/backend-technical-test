package addisonbackend

import addisonbackend.server.{BackendRoutes, BackendServer}
import addisonbackend.services.{AsyncTokenServiceLive, SimpleAsyncTokenServiceLive}
import zio.{Task, ZIO, ZIOAppDefault}

object Main extends ZIOAppDefault {

  override val run: Task[Unit] =
    ZIO
      .serviceWithZIO[BackendServer](_.start)
      .provide(
        BackendServer.layer,
        BackendRoutes.layer,
        SimpleAsyncTokenServiceLive.layer,
        AsyncTokenServiceLive.layer
      )
}
