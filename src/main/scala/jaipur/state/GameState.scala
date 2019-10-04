package jaipur.state

import jaipur.Count
import jaipur.components.Card.{Camel, GoodsCard}
import jaipur.components.Token.{BonusToken, GoodsToken}
import jaipur.components.{Card, Good, Token}

import scala.util.Random

case class Constants(
  rng: Random,
  roundsToWin: Int
)

case class GameState(
  deck: Count[Card],
  players: Map[Seat, Player],
  currentPlayer: Seat,
  market: List[Card],
  goodsTokens: Map[Good, List[GoodsToken]],
  bonusTokens: Map[Int, Count[BonusToken]],
  prevState: GameState,
  constants: Constants
) {
  val currentPlayerState: Player = players(currentPlayer)

  def updatePlayer(seat: Seat, f: Player => Player): GameState =
    copy(players = players.updatedWith(seat)(_.map(f)))

  def updateCurrentPlayer(f: Player => Player): GameState =
    updatePlayer(currentPlayer, f)
}

object GameState {
  def initialState(constants: Constants): GameState = {
    val players: Map[Seat, Player] = Seat.forPlayers(2)
    val deck: Count[Card] = Card.deck

    val deckWithoutCamels: Count[Card] = deck.update(Camel, _ - 3)
    val marketCamels: List[Card] = List.fill(3)(Camel)

    val (deckWithMarketDrawn, marketCards) = deckWithoutCamels.takeRandom(constants.rng, 2)
    val market = marketCards ++ marketCamels

    val (deckWithPlayer1Drawn, player1Hand) = deckWithMarketDrawn.takeRandom(constants.rng, 5)
    val (deckWithPlayer2Drawn, player2Hand) = deckWithPlayer1Drawn.takeRandom(constants.rng, 5)

    val playersWithHands = players.map {
      case (s@Seat(0), player1) => s -> player1.copy(
        hand = player1Hand.collect { case g: GoodsCard => g },
        camels = player1Hand.collect { case Camel => 1 }.sum
      )
      case (s@Seat(1), player2) => s -> player2.copy(
        hand = player2Hand.collect { case g: GoodsCard => g },
        camels = player2Hand.collect { case Camel => 1 }.sum
      )
    }

    GameState(
      deck = deckWithPlayer2Drawn,
      players = playersWithHands,
      currentPlayer = players.head._1,
      market = market,
      goodsTokens = Token.allGoodsTokens,
      bonusTokens = Token.allBonusTokens,
      prevState = null,
      constants = constants
    )
  }
}