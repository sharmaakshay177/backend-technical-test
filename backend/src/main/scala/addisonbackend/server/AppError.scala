package addisonbackend.server

sealed trait AppError extends Throwable

object AppError {
  final case object MissingBodyError extends AppError

  final case class JsonDecodingError(message: String) extends AppError

  final case class GenericError(message: String) extends AppError

  final case class AuthenticationError(message: String) extends AppError

}
