package jaipur.components

import enumeratum._

sealed trait Good extends EnumEntry

object Good extends Enum[Good] {
  sealed trait ExpensiveGood extends Good with EnumEntry

  object ExpensiveGood extends Enum[ExpensiveGood] {
    override def values: IndexedSeq[ExpensiveGood] = findValues
    case object Diamond extends ExpensiveGood
    case object Gold extends ExpensiveGood
    case object Silver extends ExpensiveGood
  }

  sealed trait CheapGood extends Good with EnumEntry

  object CheapGood extends Enum[CheapGood] {
    override def values: IndexedSeq[CheapGood] = findValues
    case object Cloth extends CheapGood
    case object Spice extends CheapGood
    case object Leather extends CheapGood
  }

  override def values: IndexedSeq[Good] =
    ExpensiveGood.values ++ CheapGood.values
}

