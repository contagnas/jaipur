//package jaipur.events
//
//import jaipur.Count
//import jaipur.components.Card
//import jaipur.components.Card.GoodsCard
//
//sealed trait PlayerMove
//
//object PlayerMove {
//  case class SellCards(cards: GoodsCard, number: Int) extends PlayerMove
//  case class TakeSingleGood(card: GoodsCard) extends PlayerMove
//  case class ExchangeCards(handCards: Count[Card], takenCards: Count[GoodsCard])
//  case object TakeCamels extends PlayerMove
//}
