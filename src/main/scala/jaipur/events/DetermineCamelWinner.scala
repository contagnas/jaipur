package jaipur.events

import jaipur.components.Token.CamelToken
import jaipur.state.{GameState, Player, Seat}
import monocle.function.Index._
import monocle.macros.syntax.lens._

case object DetermineCamelWinner extends Event {
  private def giveCamelToken(state: GameState, seat: Seat): GameState =
    state.lens(_.players).composeOptional(index(seat)).modify(_.lens(_.tokens).modify(CamelToken :: _))

  override def run(state: GameState): GameState = {
    val List((s1: Seat, p1: Player), (s2: Seat, p2: Player)) = state.players.toList
    if (p1.camels == p2.camels)
      state
    else if (p1.camels > p2.camels)
      giveCamelToken(state, s1)
    else
      giveCamelToken(state, s2)
  }

  override def nextEvent(state: GameState): Event = DetermineRoundWinner
}
