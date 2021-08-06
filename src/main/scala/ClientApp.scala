import akka.actor._
import scala.io.StdIn._
object ClientApp {
  final case class Connect(storage: ActorRef)
  case object Process
}
class ClientApp extends Actor {
  override def receive: Receive = {
    case ClientApp.Connect(storage) =>
      context become process(storage)
      self ! ClientApp.Process
  }
  def process(storage: ActorRef): Receive = {
    case ClientApp.Process =>
      println("Enter command: ")
      readLine().split(' ') match {
        case Array("get") => storage ! StorageApp.GetAll
        case Array("get", id) => storage ! StorageApp.Get(id)
        case Array("put", id, name) => storage ! StorageApp.Put(id, name)
        case Array("delete", id) => storage ! StorageApp.Delete(id)
        case Array("stop") => context.system.terminate()
        case _ => println("Unknown command.")
      }
      Thread.sleep(100)
      self ! ClientApp.Process
    case StorageApp.Result(id, name) => println(s"Received: $id -> $name")
    case StorageApp.Ack => println("Received ack")
  }
}
