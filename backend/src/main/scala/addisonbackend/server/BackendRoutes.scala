package addisonbackend.server

import addisonbackend.models.Credentials
import addisonbackend.server.ServerUtils.parseBody
import addisonbackend.services.SimpleAsyncTokenService
import zhttp.http._
import zio.{URLayer, ZIO, ZLayer}
import zio.json.EncoderOps

final case class BackendRoutes(service: SimpleAsyncTokenService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "ping" =>
      ZIO.attempt(Response.json("pong"))

    case req @ Method.GET -> !! / "getToken" =>
      for {
        credentials <- parseBody[Credentials](req)
        token       <- service.requestToken(credentials)
      } yield Response.json(token.toJson)
  }

}

object BackendRoutes {
  val layer: URLayer[SimpleAsyncTokenService, BackendRoutes] =
    ZLayer.fromFunction(BackendRoutes.apply _)
}
