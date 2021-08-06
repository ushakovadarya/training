import akka.actor._
import scala.concurrent.duration._
import language.postfixOps
object NewExample extends App {
  case object You
  case object NotYou
  class Your extends Actor{
    var iter = 20
    def receive = {
      case NotYou => {
        println("You!")
         if(iter > 0){
           iter -= 1
           sender() ! You
         }
        else{
           println("Enough!")
           sender() ! PoisonPill
           self ! PoisonPill
         }
      }
    }
  }

  class NotYour(your: ActorRef) extends Actor {
    def receive = {
      case You => {
        println("Not your!")
        your ! NotYou
      }
    }
  }

  val system = ActorSystem("younotyou")
  val your = system.actorOf(Props[Your], "your")
  val notyour = system.actorOf(Props(classOf[NotYour], your), "notyour")

  import system.dispatcher
  system.scheduler.scheduleOnce(100 millis){
    notyour ! You
  }
}
