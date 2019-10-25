package jaipur.state

import jaipur.Count
import jaipur.components.Token.{BonusToken, GoodsToken}
import jaipur.components.{Card, Good, Token}
import jaipur.events.{DealCards, Event}

import scala.util.Random

case class Constants(
  rng: Random,
  roundsToWin: Int
)

case class GameState(
  deck: Count[Card],
  players: Map[Seat, Player],
  currentPlayer: Seat,
  market: Count[Card],
  goodsTokens: Map[Good, List[GoodsToken]],
  bonusTokens: Map[Int, Count[BonusToken]],
  prevState: GameState,
  constants: Constants,
  nextEvent: Event
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

    GameState(
      deck = deck,
      players = players,
      currentPlayer = players.head._1,
      market = Count.empty,
      goodsTokens = Token.allGoodsTokens,
      bonusTokens = Token.allBonusTokens,
      prevState = null,
      constants = constants,
      nextEvent = DealCards
    )
  }
}