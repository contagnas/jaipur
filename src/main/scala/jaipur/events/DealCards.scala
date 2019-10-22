package jaipur.events

import jaipur.Count
import jaipur.components.Card
import jaipur.components.Card.{Camel, GoodsCard}
import jaipur.state.{GameState, Player, Seat}

import cats.implicits._

case object DealCards extends Event {
  override def run(state: GameState): GameState = {
    val deck = Card.deck

    val deckWithoutCamels: Count[Card] = deck.update(Camel, _ - 3)
    val marketCamels: Count[Card] = Count(Camel -> 3)

    val (deckWithMarketDrawn, marketCards) = deckWithoutCamels.takeRandom(state.constants.rng, 2)
    val market = marketCards |+| marketCamels

    val (deckWithPlayer1Drawn, player1Hand) = deckWithMarketDrawn.takeRandom(state.constants.rng, 5)
    val (deckWithPlayer2Drawn, player2Hand) = deckWithPlayer1Drawn.takeRandom(state.constants.rng, 5)

    val playersWithHands: Map[Seat, Player] = state.players.map {
      case (s@Seat(0, _), player1) => s -> player1.copy(
        hand = player1Hand.collectKeys { case g: GoodsCard => g },
        camels = player1Hand.totalWhere(_ == Camel)
      )
      case (s@Seat(1, _), player2) => s -> player2.copy(
        hand = player2Hand.collectKeys { case g: GoodsCard => g },
        camels = player2Hand.totalWhere(_ == Camel)
      )
    }

    state.copy(
      players = playersWithHands,
      market = market,
      deck = deckWithPlayer2Drawn
    )
  }

  override def nextEvent(state: GameState): Event = GetPlayerInput[ChooseAction]
}
