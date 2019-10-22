package jaipur.events

import jaipur.state.GameState
import simulacrum.typeclass

trait Event {
  type InvalidMessage = String
  def validationError(state: GameState): Option[InvalidMessage] = None
  def run(state: GameState): GameState
  def nextEvent(state: GameState): Event
}

@typeclass trait Enumerable[E <: Event] {
  def allValidMoves(state: GameState): Set[E] =
    allPossibleMoves.filter {
      move => move.validationError(state).isEmpty
    }.toSet

  def allPossibleMoves: Seq[E] =
    throw new NotImplementedError("You need to implement at least one method in Enumerable")
}
