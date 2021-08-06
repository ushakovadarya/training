import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn
import scala.util._

object RouteMin {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    lazy val helloRoute = {
      get {
        val name = "Artificial Intelligence"
        val random = new Random()
        val numb = random.nextInt(10)
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Hello, this is $name, your number is $numb"))
      }
    }
    lazy val usersRoute = {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>This is page Users</h1>"))
      }
    }
    val route = {
      concat(
        path("hello")(helloRoute),
        path("users")(usersRoute)
      )
    }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
bindingFuture.onComplete{
  case Success(binding) =>
    val address = binding.localAddress
    println(s"Server online at http://${address.getHostString}:${address.getPort}/")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  case Failure(ex) =>
    println("Failed to bind HTTP endpoint, terminating system", ex)
    system.terminate()
}

  }
}
