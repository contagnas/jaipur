package jaipur

import cats.Monoid
import cats.implicits._

import scala.collection.mutable
import scala.util.Random

object Count {
  def apply[K](values: (K, Int)*): Count[K] = {
    values.foreach(kv => if (kv._2 < 0) throw new NegativeCountException(kv.toString))
    val filtered = values.filter(_._2 > 0)
    new Count(filtered.toMap)
  }

  def ofList[K](ks: List[K]): Count[K] = new Count(
    ks.groupBy(identity).view.mapValues(_.size).toMap
  )

  def empty[K] = new Count(Map.empty[K, Int])

  implicit def CountSemigroup[K]: Monoid[Count[K]] = new Monoid[Count[K]] {
    override def combine(x: Count[K], y: Count[K]) =
      new Count(x.map |+| y.map)

    override def empty: Count[K] = Count.empty
  }

  class NegativeCountException(msg: String) extends Exception(s"No negative numbers allowed in Count $msg")

  private def choiceHelper[K](count: Count[K]): Seq[Count[K]] = {
    val seen = mutable.Map[Count[K], Seq[Count[K]]](Count.empty[K] -> Seq(Count.empty[K]))

    def recursiveChoices(rCount: Count[K]): Seq[Count[K]] = {
      seen.getOrElse(rCount, {
        val countMap = rCount.map
        val (key, keyCount) = countMap.head
        val countExceptKey = new Count(countMap - key)
        val subChoices = recursiveChoices(countExceptKey)

        val choices = for {
          n <- 0 to keyCount
          subChoice <- subChoices
        } yield subChoice.set(key, n)
        seen.update(rCount, choices)
        choices
      })
    }

    recursiveChoices(count)
  }
}

class Count[K] private(private val map: Map[K, Int]) {
  def get(item: K): Int = map.getOrElse(item, 0)

  def total: Int = map.values.sum

  def set(item: K, count: Int): Count[K] =
    if (count < 0) throw new Count.NegativeCountException(s"setting $item to $count")
    else if (count == 0) new Count(map - item)
    else new Count(map.updated(item, count))

  def update(item: K, f: Int => Int): Count[K] =
    set(item, f(get(item)))

  def addAll[L <: K](other: Count[L]): Count[K] = other.map.foldLeft(this) {
    case (acc, (k, v)) => acc.update(k, _ + v)
  }

  def subtractAll[L <: K](other: Count[L]): Count[K] = {
    if (!other.subsetOf(this))
      throw new Count.NegativeCountException("- subtractAll: subtracted count has keys not present in other.")

    other.map.foldLeft(this) { case (acc, (k, v)) =>
      acc.update(k, _ - v)
    }
  }

  lazy val choices: Seq[Count[K]] = Count.choiceHelper(this)

  def map[L](f: K => L): Count[L] = new Count[L](
    map.map { case (k, v) => f(k) -> v }
  )

  def contains(item: K): Boolean = map.contains(item)

  val exists: K => Boolean = contains

  def totalWhere(p: K => Boolean): Int =
    map.collect { case (k, v) if p(k) => v }.sum

  def nonZeroItems: Set[K] = map.keySet

  def toList: List[(K, Int)] = map.toList

  def collectKeys[L](pf: PartialFunction[K, L]): Count[L] = new Count(
    map.collect {
      case (k, v) if pf.isDefinedAt(k) => pf(k) -> v
    }
  )

  def subsetOf[L >: K](other: Count[L]): Boolean = map.forall { case (k, v) => other.get(k) >= v }

  def takeRandom(rng: Random): (Count[K], K) = {
    val selectedWeight = rng.between(0, total)

    var countSeen = 0
    for ((item, count) <- map) {
      countSeen += count
      if (countSeen >= selectedWeight)
        return (update(item, _ - 1), item)
    }
    throw new IllegalStateException(s"Found no item in CDF with total $total and selected weight $selectedWeight")
  }

  def takeRandom(rng: Random, number: Int): (Count[K], Count[K]) =
    (1 to number).foldLeft((this, Count.empty[K])) { case ((count, draws), _) =>
      if (count.map.isEmpty) {
        (count, draws)
      } else {
        val (removed, drawn) = count.takeRandom(rng)
        (removed, draws.update(drawn, _ + 1))
      }
    }

  override def toString: String = {
    val valueStrings = map.map { case (k, v) => s"$k -> $v" }.mkString(", ")
    s"Count($valueStrings)"
  }

  override def equals(obj: Any): Boolean =
    obj.isInstanceOf[Count[K]] && obj.asInstanceOf[Count[K]].map == this.map

  override def hashCode(): Int = this.map.hashCode()
}
