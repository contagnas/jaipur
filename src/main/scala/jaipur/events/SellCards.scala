package jaipur.events

import jaipur.Count
import jaipur.components.Card.GoodsCard
import jaipur.components.Good.ExpensiveGood
import jaipur.components.Token.BonusToken
import jaipur.state.GameState

case class SellCards(card: GoodsCard, number: Int) extends Event {
  override def validationError(state: GameState): Option[InvalidMessage] =
    if (state.currentPlayerState.hand.get(card) < number)
      Some(s"You do not have ${number} ${card}s to sell")
    else (card.good, number) match {
      case (_: ExpensiveGood, n) if n < 2 =>
        Some("When selling expensive goods, you must sell at least 2.")
      case (_, n) if n < 1 =>
        Some("You must sell at least one good.")
      case _ => None
    }

  override def run(state: GameState): GameState = {
    val (remainingBonus, bonus) = {
      if (number >= 3) {
        val bonusNumber = math.min(number, 5)
        val (remaining: Count[BonusToken], bonus: BonusToken) = state.bonusTokens(bonusNumber)
          .takeRandom(state.constants.rng)
        (state.bonusTokens.updated(number, remaining), Some(bonus))
      } else (state.bonusTokens, None)
    }

    val goodsTokens = state.goodsTokens.getOrElse(card.good, Nil)
    val takenTokens = goodsTokens.take(number)
    val remainingTokens = goodsTokens.drop(number)
    val tokensEarned = takenTokens ++ bonus

    state.updateCurrentPlayer(
      p => p.copy(
        tokens = p.tokens ++ tokensEarned,
        hand = p.hand.update(card, _ - number)
      )
    ).copy(
      goodsTokens = state.goodsTokens.updated(card.good, remainingTokens),
      bonusTokens = remainingBonus
    )
  }

  override def nextEvent(state: GameState): Event = EndTurn
}
