package jaipur.state

import jaipur.Count
import jaipur.components.Card.GoodsCard
import jaipur.components.Token

case class Player(
  hand: Count[GoodsCard] = Count.empty,
  tokens: List[Token] = Nil,
  roundsWon: Int = 0,
  camels: Int = 0,
)

class Seat private (val playerNumber: Int, numberOfPlayers: Int) {
  def nextPlayer: Seat = {
    val nextPlayerNumber: Int = (playerNumber + 1) % numberOfPlayers
    new Seat(nextPlayerNumber, numberOfPlayers)
  }
}

object Seat {
  def unapply(arg: Seat): Option[Int] = Some(arg.playerNumber)

  def forPlayers(numberOfPlayers: Int): Map[Seat, Player] = {
    (0 until numberOfPlayers).map(seatNumber =>
      new Seat(seatNumber, numberOfPlayers) -> Player()
    ).toMap
  }
}
