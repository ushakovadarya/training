import akka.actor._
object Question {
  class Question extends Actor {
    var counter = 10
    def receive = {
      case "answer" => {
        if (counter >0) {
          counter -=1
          println("Question?")
          sender() ! "question"
        }
        else {
          println("Thank you! :)")
          context.system.terminate()
        }
      }
      case _      => println("received unknown message")
    }
  }
}
