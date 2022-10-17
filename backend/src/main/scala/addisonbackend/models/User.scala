package addisonbackend.models

final case class User(userId: String)

object User {
  def make(userId: String): User = User(userId)
}
