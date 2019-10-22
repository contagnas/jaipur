package jaipur

import jaipur.components.Good.CheapGood.{Cloth, Leather}
import jaipur.components.Good.ExpensiveGood.{Diamond, Silver}

object TestCount extends App {
  val hand = Count(
    Diamond -> 3,
    Cloth -> 2,
    Leather -> 1,
    Silver -> 100,
  )

  println(s"hand.choices = ${hand.choices}")
  println(s"hand.choices.size = ${hand.choices.size}")
}
