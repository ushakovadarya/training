import akka.actor._
object Answer {
class Answer(question: ActorRef) extends Actor{
  def receive = {
    case "question" => {
      println("Answer")
      question ! "answer"
    }
    case "stop" => {
      context.system.terminate()
    }
    case _ => println("Unknown command")
  }
}
}
