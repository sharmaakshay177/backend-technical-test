package addisonbackend.server

import zhttp.http.Request
import zio.{IO, ZIO}
import zio.json._

object ServerUtils {

  def parseBody[A: JsonDecoder](request: Request): IO[AppError, A] =
    for {
      body   <- request.bodyAsString.orElseFail(AppError.MissingBodyError)
      parsed <- ZIO.from(body.fromJson[A]).mapError(AppError.JsonDecodingError)
    } yield parsed

}
