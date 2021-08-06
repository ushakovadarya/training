import akka.actor._
import akka.util.Timeout
import akka.pattern._
import scala.concurrent.duration._

import scala.util.Random

object Barista {
  case object EspressoRequest
  case object ClosingTime
  case object ComebackLater
  case class EspressoCup(state: EspressoCup.State)

  object EspressoCup {
    sealed trait State
    case object Clean extends State
    case object Filled extends State
    case object Dirty extends State
  }

  case class Receipt(amount: Int)
}

class Barista extends Actor {
  import Barista._
  import Register._
  import EspressoCup._
  import context.dispatcher

  implicit val timeout = Timeout(4.seconds)

  val register = context.actorOf(Props[Register], "Register")

  def receive = {
    case EspressoRequest =>
      val receipt = register ? Transaction(Espresso)
      receipt.map((EspressoCup(Filled), _)).recover {
        case _: AskTimeoutException => ComebackLater
      } pipeTo(sender)
    case ClosingTime => context.system.stop(self)
  }
}

object Register {
  sealed trait Article
  case object Espresso extends Article
  case object Cappuccino extends Article
  case class Transaction(article: Article)
  class PaperJamException(msg: String) extends Exception(msg)
}

class Register extends Actor {
  import Register._
  import ReceiptPrinter._
  import Barista._
  import context.dispatcher

  implicit val timeout = Timeout(4.seconds)

  var revenue = 0
  val prices = Map[Article, Int](Espresso -> 150, Cappuccino -> 250)
  val printer = context.actorOf(Props[ReceiptPrinter], "Printer")

  override def postRestart(reason: Throwable) {
    super.postRestart(reason)
    println(s"Restarted, and revenue is $revenue cents")
  }

  def receive = {
    case Transaction(article) =>
      val price = prices(article)
      val requester = sender
      (printer ? PrintJob(price)).map((requester, _)).pipeTo(self)
    case (requester: ActorRef, receipt: Receipt) =>
      revenue += receipt.amount
      println(s"revenue is $revenue cents")
      requester ! receipt
  }
}

object ReceiptPrinter {
  case class PrintJob(amount: Int)
  class PaperJamException(msg: String) extends Exception(msg)
}

class ReceiptPrinter extends Actor {
  import ReceiptPrinter._
  import Barista._
  var paperJam = false

  override def postRestart(reason: Throwable) {
    super.postRestart(reason)
    println(s"Restarted, paper jam == $paperJam")
  }

  def receive = {
    case PrintJob(amount) => sender ! createReceipt(amount)
  }

  def createReceipt(price: Int): Receipt = {
    if (Random.nextBoolean()) paperJam = true
    if (paperJam) throw new PaperJamException("OMG, not again!")
    Receipt(price)
  }
}

object Customer {
  case object CaffeineWithdrawalWarning
}

class Customer(coffeeSource: ActorRef) extends Actor {
  import Barista._
  import Barista.EspressoCup._
  import Customer._
  import context.dispatcher

  context.watch(coffeeSource)

  def receive = {
    case CaffeineWithdrawalWarning => coffeeSource ! EspressoRequest
    case (EspressoCup(Filled), Receipt(amount)) =>
      println(s"yay, caffeine for ${self.path.name}!")
    case ComebackLater =>
      println("grumble, grumble")
      context.system.scheduler.scheduleOnce(300.millis) {
        coffeeSource ! EspressoRequest
      }
    case Terminated(barista) =>
      println("Oh well, let's find another coffeehouse...")
  }
}

object BaristaAkka extends App {
  import Customer._

  val system = ActorSystem("Coffeehouse")

  val barista = system.actorOf(Props[Barista], "Barista")
  val customerJohnny = system.actorOf(Props(classOf[Customer], barista), "Johnny")
  val customerAlina = system.actorOf(Props(classOf[Customer], barista), "Alina")

  customerJohnny ! CaffeineWithdrawalWarning
  customerAlina ! CaffeineWithdrawalWarning
}
