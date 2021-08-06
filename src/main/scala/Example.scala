import akka.actor._
import StorageApp._
import ClientApp._
object Example extends App {
  val system = ActorSystem("StorageClient")
  val storage = system.actorOf(Props[StorageApp], "storage")
  val client = system.actorOf(Props[ClientApp], "client")
  client ! Connect(storage)
}
