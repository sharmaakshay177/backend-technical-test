package addisonbackend.server

import zhttp.http.HttpApp
import zhttp.service.Server
import zio.{ZIO, ZLayer}

final case class BackendServer(
    backendRoutes: BackendRoutes
) {

  val allRoutes: HttpApp[Any, Throwable] =
    backendRoutes.routes

  def start: ZIO[Any, Throwable, Unit] =
    Server.start(8080, allRoutes)

}

object BackendServer {
  val layer: ZLayer[BackendRoutes, Nothing, BackendServer] =
    ZLayer.fromFunction(BackendServer.apply _)
}
