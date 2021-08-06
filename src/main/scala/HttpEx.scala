import akka.http.scaladsl.model.headers.`Raw-Request-URI`
import akka.http.scaladsl.model._
object HttpEx {
  def main(args: Array[String]): Unit = {
    val req = HttpRequest(uri = "/ignored", headers = List(`Raw-Request-URI`("/a/b%2Bc")))
    println(req)
  }
}
