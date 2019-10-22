package jaipur.events

import jaipur.components.Card.Camel
import jaipur.state.GameState
import cats.implicits._

case object TakeCamels extends Event {
  override def validationError(state: GameState): Option[TakeCamels.InvalidMessage] =
    if (!state.market.contains(Camel))
      Some("The market has no camels to take.")
    else None

  override def run(state: GameState): GameState = {
    val numberOfCamels = state.market.get(Camel)
    val cardsToDraw = math.min(numberOfCamels, state.deck.total)
    val (remainingDeck, drawnCards) = state.deck.takeRandom(state.constants.rng, cardsToDraw)

    state.updateCurrentPlayer(
      p => p.copy(camels = p.camels + numberOfCamels)
    ).copy(
      deck = remainingDeck,
      market = state.market.set(Camel, 0) |+| drawnCards
    )
  }

  override def nextEvent(state: GameState): Event = EndTurn

  implicit val enum = new Enumerable[TakeCamels.type] {
    override def allPossibleMoves: Seq[TakeCamels.type] = Seq(TakeCamels)
  }
}
