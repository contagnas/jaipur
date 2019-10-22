package jaipur

import jaipur.events.{DealCards, Event, GameOver}
import jaipur.state.{Constants, GameState}

import scala.util.Random

object Main extends App {
  var gamesPlayed = 0
  while (true) {
    val seed = System.currentTimeMillis()
    println(s"gamesPlayed = ${gamesPlayed}")
    gamesPlayed = gamesPlayed + 1

    val gameState = GameState.initialState(Constants(rng = new Random(seed), roundsToWin = 3))
    println(s"seed = ${seed}")

    val eventNumber = LazyList.continually(1)
      .scanLeft(0)(_ + _)

    eventNumber
      .scanLeft[(GameState, Event)](gameState, DealCards) {
        case ((state, event), i) =>
//          println(s"i = ${i}")
//          println(s"event = ${event}")
          //        println(s"event = ${event}")
          //        println(s"state.currentPlayerState.hand = ${state.currentPlayerState.hand}")
          val nextState = event.run(state)
          val nextEvent = event.nextEvent(nextState)
          (nextState, nextEvent)
      }.find {
      case (state, GameOver(winner)) =>
        println(s"winner = ${winner}")
        true
      case _ => false
    }
  }
}
