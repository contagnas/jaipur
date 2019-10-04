package jaipur.components

import jaipur.Count
import jaipur.components.Good.CheapGood._
import jaipur.components.Good.ExpensiveGood._

sealed abstract class Token(val points: Int)

object Token {
  case object CamelToken extends Token(5)
  case class GoodsToken(good: Good, override val points: Int) extends Token(points)

  private def goodsTokens(good: Good, points: List[Int]) =
    good -> points.map(GoodsToken(good, _))

  val allGoodsTokens: Map[Good, List[GoodsToken]] = Map(
    goodsTokens(Diamond, List(7, 7, 5, 5, 5)),
    goodsTokens(Gold, List(6, 6, 5, 5, 5)),
    goodsTokens(Silver, List(5, 5, 5, 5, 5)),
    goodsTokens(Cloth, List(3, 3, 3, 2, 2, 1, 1)),
    goodsTokens(Spice, List(5, 3, 3, 2, 2, 1, 1)),
    goodsTokens(Leather, List(4, 3, 2, 1, 1, 1, 1, 1, 1)),
  )

  case class BonusToken(combo: Int, override val points: Int) extends Token(points)

  private def bonusTokens(combo: Int, points: Count[Int]) =
    combo -> points.map(BonusToken(combo, _))

  // TODO: Replace these with the real numbers,
  val allBonusTokens: Map[Int, Count[BonusToken]] = Map(
    bonusTokens(3, Count(1 -> 3, 2 -> 2, 3 -> 1)),
    bonusTokens(4, Count(4 -> 3, 5 -> 2, 6 -> 1)),
    bonusTokens(5, Count(7 -> 3, 8 -> 2, 9 -> 1)),
  )
}
