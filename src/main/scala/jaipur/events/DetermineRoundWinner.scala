package jaipur.events

import jaipur.components.Token.BonusToken
import jaipur.state.{GameState, Player}
import monocle.function.Index._
import monocle.macros.GenLens
import monocle.macros.syntax.lens._

case object DetermineRoundWinner extends Event {
  private def roundWinnerMetric(player: Player) = (
    player.tokens.map(_.points).sum,
    player.tokens.collect { case _: BonusToken => 1 }.size,
    player.tokens.size
  )
  override def run(state: GameState): GameState = {
    val List((loserSeat, _), (winnerSeat, _)) =
      state.players.toList.sortBy {
        case (_, p) => roundWinnerMetric(p)
      }

    state.lens(_.players)
      .composeOptional(index(winnerSeat))
      .composeLens(GenLens[Player](_.roundsWon))
      .modify(_ + 1)
      .lens(_.currentPlayer).set(loserSeat)
  }

  override def nextEvent(state: GameState): Event = {
    val roundsWon = state.players.values.map(_.roundsWon).max
    if (roundsWon >= state.constants.roundsToWin)
      GameOver(state.players.maxBy(_._2.roundsWon)._1)
    else
      DealCards
  }
}
