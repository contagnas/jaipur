package jaipur.events

import jaipur.components.Card.GoodsCard
import jaipur.components.Good
import jaipur.state.GameState

case class TakeSingleCard(card: GoodsCard) extends Event {
  override def validationError(state: GameState): Option[InvalidMessage] =
    if (state.currentPlayerState.hand.total >= 7)
      Some("You may only have 7 cards in your hand.")
    else if (!state.market.contains(card))
      Some(s"$card is not available in the market.")
    else None

  override def run(state: GameState): GameState = {
    val (remainingDeck, drawnCard) = state.deck.takeRandom(state.constants.rng, 1)
    val remainingMarket = state.market.update(card, _ - 1)

    state.updateCurrentPlayer(
      p => p.copy(hand = p.hand.update(card, _ + 1))
    ).copy(
      deck = remainingDeck,
      market = remainingMarket.addAll(drawnCard)
    )
  }

  override def nextEvent(state: GameState): Event = EndTurn
}

object TakeSingleCard {
  implicit val enum = new Enumerable[TakeSingleCard] {
    override def allPossibleMoves: Seq[TakeSingleCard] =
      Good.values.map(GoodsCard.apply).map(TakeSingleCard.apply)
  }
}

