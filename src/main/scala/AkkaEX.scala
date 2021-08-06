import akka.actor._
import scala.io.StdIn._
import Answer._
import Question._
object AkkaEX extends App{
  val system = ActorSystem("system")
  val example = system.actorOf(Props[Question], "example")
  val answer = system.actorOf(Props(classOf[Answer], example), "answer")
  answer ! readLine("Enter command:")

}
