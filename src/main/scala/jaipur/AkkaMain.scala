package jaipur

import akka.actor.typed
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, Actor}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import akka.persistence.typed.{ExpectingReply, PersistenceId}
import akka.stream.ActorMaterializer
import jaipur.events.Event
import jaipur.state.{Constants, GameState}

import scala.io.StdIn
import scala.util.Random

object GameManager {
  sealed trait Command

  def apply: Behavior[Command] = Actor.deferred {

  }
}

object AkkaMain {
  case class Command[E, R](event: E, override val replyTo: ActorRef[R]) extends ExpectingReply[R]

  type JaipurAkka = Command[Event, Either[String, GameState]]

  def makeGame(game: String): EventSourcedBehavior[JaipurAkka, JaipurAkka, GameState] = game match {
    case "foobar" =>
    case "jaipur" =>
      EventSourcedBehavior[JaipurAkka, JaipurAkka, GameState](
        persistenceId = PersistenceId("jaipur-1"),
        emptyState = GameState.initialState(Constants(new Random, 2)),
        commandHandler = (state, command) =>
          command.event.validationError(state) match {
            case None => Effect.persist(command).thenReply(command)(Right.apply)
            case Some(error) => Effect.unhandled.thenReply(command)(_ => Left(error))
          },
        eventHandler = (state, event) =>
          event.event.persist(state)
      )
  }

  object GameManager {
    def apply: Behavior[Nothing] =
      Behaviors.setup[Nothing](context => new GameManager(context))
  }

  class GameManager(context: ActorContext[Nothing]) extends AbstractBehavior[Nothing] {
    context.log.info("Starting game manager")

    override val createReceive: Receive[Nothing] = {
      case _: Nothing => ()
    }
  }

  def main(args: Array[String]): Unit = {
//    implicit val system = ActorSystem("Table")
//    implicit val materializer = ActorMaterializer()
//    implicit val executionContext = system.dispatcher

    import akka.actor.typed.scaladsl.adapter._

    val httpBehavior = Behaviors.setup { actorContext =>
      implicit val actorSystem = actorContext.system.toUntyped
      implicit val materializer = ActorMaterializer()

      val route = path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hi!</h1>"))
        }
      }

      val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    }


//    val bf = Http().bindAndHandle(route, "localhost", 8080)


    println("Running.")

    StdIn.readLine()

    //    val game: Behavior[JaipurAkka] = makeGame("jaipur")
    //    game

//    bf.flatMap(_.unbind())
//      .onComplete(_ => system.terminate())
    //    val game = makeGame("jaipur")
  }
}

