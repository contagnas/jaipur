package jaipur.components

import enumeratum._
import jaipur.Count
import jaipur.components.Good.CheapGood._
import jaipur.components.Good.ExpensiveGood._

sealed trait Card extends EnumEntry

object Card extends Enum[Card] {
  case object Camel extends Card
  case class GoodsCard(good: Good) extends Card

  override def values: IndexedSeq[Card] =
    Good.values.map(GoodsCard) :+ Camel

  val deck: Count[Card] = Count(
    GoodsCard(Diamond) -> 6,
    GoodsCard(Gold) -> 6,
    GoodsCard(Silver) -> 6,
    GoodsCard(Cloth) -> 8,
    GoodsCard(Spice) -> 8,
    GoodsCard(Leather) -> 10,
    Camel -> 11,
  )
}

