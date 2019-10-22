package jaipur.events
import jaipur.state.{GameState, Seat}

case class GameOver(winner: Seat) extends Event { self =>
  override def run(state: GameState): GameState = state
  override def nextEvent(state: GameState): Event = self
}
