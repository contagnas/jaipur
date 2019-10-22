package jaipur.events

import jaipur.state.GameState

case object EndTurn extends Event {
  override def run(state: GameState): GameState = state.copy(currentPlayer = state.currentPlayer.nextPlayer)
  override def nextEvent(state: GameState): Event = {
    if (state.market.total < 5 || state.goodsTokens.count(_._2.isEmpty) >= 3)
      EndRound
    else
      GetPlayerInput[ChooseAction]
  }
}
