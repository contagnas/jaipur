package jaipur.events

import jaipur.state.GameState

case object EndTurn extends Event {
  override def run(state: GameState): GameState = state

  override def nextEvent(state: GameState): Event = ???
}
