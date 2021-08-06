import akka.actor._
import scala.io.StdIn._
object Storage {
  final case class Get(key: String)
  final case class Put(key:String, value: String)
  final case class Delete(key:String)

  final case class Result(key: String, value: Option[String])
  case object Ack
  case object GetAll
}
class Storage extends Actor{
  import Storage._
  override def receive: Receive = process(Map.empty)
  def process(store: Map[String, String]): Receive = {
    case GetAll =>
      println(store)
    case Get(key) =>
      sender() ! Result(key, store.get(key))
    case Put(key, value) =>
      context become process(store + (key->value))
      sender() ! Ack
    case Delete(key) =>
      context become process(store - key)
      sender() ! Ack
  }
}
object Client {
  final case class Connect(storage: ActorRef)
  case object Process
}
class Client extends Actor {
  override def receive: Receive = {
    case Client.Connect(storage) =>
      context become process(storage)
      self ! Client.Process
  }
  def process(storage: ActorRef): Receive = {
    case Client.Process =>
      println("Enter command: ")
      readLine().split(' ') match {
        case Array("get") => storage ! Storage.GetAll
        case Array("get", key) => storage ! Storage.Get(key)
        case Array("put", key, value) => storage ! Storage.Put(key, value)
        case Array("delete", key) => storage ! Storage.Delete(key)
        case Array("stop") => context.system.terminate()
        case _ => println("Unknown command")
      }
      Thread.sleep(100)
      self ! Client.Process
    case Storage.Result(key, value) => println(s"Received: $key -> $value")
    case Storage.Ack => println("Received ack")
  }
}
object StorageClient extends App{
  val system = ActorSystem("system")
  val storage = system.actorOf(Props[Storage], "storage")
  val client = system.actorOf(Props[Client], "client")
  client ! Client.Connect(storage)
}
