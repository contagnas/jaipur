package jaipur.events

import jaipur.state.GameState

case class GetPlayerInput[E <: Event : Enumerable]() extends Event {
  override def run(state: GameState): GameState = state

  override def nextEvent(state: GameState): Event = {
    val possibleMoves = Enumerable[E].allValidMoves(state)
    possibleMoves.size match {
      case 0 =>
//        println("No moves possible")
        throw new IllegalArgumentException("Waiting for player move when none are possible")
      case 1 =>
//        println(s"Only one move is possible, selecting it automatically: ${possibleMoves.head}")
        possibleMoves.head
      case _ =>
        val move = state.constants.rng.shuffle(possibleMoves).head
//        println(s"Player ${state.currentPlayer}: $move")
        move
    }
  }
}

