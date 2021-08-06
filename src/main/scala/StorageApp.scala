import akka.actor._
object StorageApp {
  final case class Get(id: String)
  final case class Put(id: String, name: String)
  final case class Delete(id: String)
  final case class Result(id: String, name: Option[String])

  case object Ack
  case object GetAll
}
class StorageApp extends Actor{
  import StorageApp._
  override def receive: Receive = process(Map.empty)
  def process(store: Map[String, String]): Receive = {
    case Get(id) => {
      sender() ! Result(id, store.get(id))
    }
    case Put(id, name) => {
      context become process(store + (id->name))
      sender() ! Ack
    }
    case Delete(id) => {
      context become process(store - id)
      sender() ! Ack
    }
    case GetAll => println(store)
  }
}
