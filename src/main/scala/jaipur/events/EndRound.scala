package jaipur.events
import jaipur.state.GameState

object EndRound extends Event {
  override def run(state: GameState): GameState = state
  override def nextEvent(state: GameState): Event = DetermineCamelWinner
}
