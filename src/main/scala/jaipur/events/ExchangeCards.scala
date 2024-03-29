package jaipur.events

import jaipur.Count
import jaipur.components.Card
import jaipur.components.Card.{Camel, GoodsCard}
import jaipur.state.GameState

case class ExchangeCards(fromHand: Count[GoodsCard], camelsExchanged: Int, fromMarket: Count[GoodsCard]) extends Event {
  override def validationError(state: GameState): Option[InvalidMessage] = {
    val marketGoodsCards = fromMarket.collectKeys { case g: GoodsCard => g }
    if (fromMarket.total < 2)
      Some("You must take at least two cards from the market.")
    else if (state.currentPlayerState.camels < camelsExchanged)
      Some(s"You do not have $camelsExchanged camels.")
    else if (fromHand.total + camelsExchanged != fromMarket.total)
      Some("Must exchange equal number of cards between hand/camels and market.")
    else if (!fromHand.subsetOf(state.currentPlayerState.hand))
      Some(s"You do not have all of $fromHand in your hand.")
    else if (!fromMarket.subsetOf(state.market))
      Some(s"The market does not have all of $fromMarket.")
    else if (fromHand.nonZeroItems.intersect(marketGoodsCards.nonZeroItems).nonEmpty)
      Some("You may not exchange a card in your hand for the same card from the market.")
    else if (camelsExchanged + state.currentPlayerState.hand.total > 7)
      Some("You may not exchange camels to put more than 7 cards in your hand.")
    else None
  }

  override def run(state: GameState): GameState = {
    state.copy(
      market = state.market.subtractAll(fromMarket).addAll(fromHand).update(Camel, _ + camelsExchanged)
    ).updateCurrentPlayer(
      p => p.copy(
        hand = p.hand.subtractAll(fromHand).addAll(fromMarket),
        camels = p.camels - camelsExchanged
      )
    )
  }

  override def nextEvent(state: GameState): Event = EndTurn
}

object ExchangeCards {
  implicit val enum = new Enumerable[ExchangeCards] {
    override def allValidMoves(state: GameState): Set[ExchangeCards] = {
      val marketGoodsCards = state.market.collectKeys { case g: GoodsCard => g }
      val allPossible = for {
        hand <- state.currentPlayerState.hand.choices
        market <- marketGoodsCards.choices
        camelsNeeded = market.total - hand.total
        if camelsNeeded >= 0 && camelsNeeded <= state.currentPlayerState.camels
      } yield ExchangeCards(hand, camelsNeeded, market)
      allPossible.filter(_.validationError(state).isEmpty)
    }.toSet
  }
}