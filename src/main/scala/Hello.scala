import scala.util.Try
import scala.io.StdIn._
import java.net.URL
import scala.concurrent._
object Hello extends App {
  implicit def fuc(s:String) = s.hashCode
  println(fuc("t"))
  case class User(
                   id: Int,

                   firstName: String,

                   lastName: String,

                   age: Int,

                   gender: Option[String])

  object UserRepository {

    private val users = Map(1 -> User(1, "John", "Doe", 32, Some("male")),
      2 -> User(2, "Johanna", "Doe", 30, None),
      3 -> User(3, "Katya", "Qwi", 25, Some("female")))
    def findById(id: Int): Option[User] = users.get(id)
    def findAll = users.values
  }
  val res = for {
    User(_, _, _, _, Some(gender)) <- UserRepository.findAll
  } yield gender
  res.foreach(println(_))
  /*def parseURL(url: String): Try[URL] = Try(new URL(url))
  def updateName(name: String): Either[String, String] = {
    if(name.isEmpty) Left("Error: name is empty")
    else Right(name)
  }
  val urlparse = "http://yandex.ru"
  println(parseURL(readLine("URL: ")) getOrElse(new URL("http://yandex.ru")))
  println(parseURL(urlparse).map(_.getProtocol))
  println(updateName(readLine("Name: ")))*/

}
