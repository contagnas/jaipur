package jaipur.events
import enumeratum._
import jaipur.state.GameState

sealed abstract class ActionChoice extends EnumEntry

object ActionChoice extends Enum[ActionChoice] {
  override def values: IndexedSeq[ActionChoice] = findValues
  case object TakeCard extends ActionChoice
  case object Exchange extends ActionChoice
  case object TakeCamels extends ActionChoice
  case object Sell extends ActionChoice
}

case class ChooseAction(choice: ActionChoice) extends Event {
  override def validationError(state: GameState): Option[InvalidMessage] = {
    val enum = choice match {
      case ActionChoice.TakeCard => Enumerable[TakeSingleCard]
      case ActionChoice.Exchange => Enumerable[ExchangeCards]
      case ActionChoice.Sell => Enumerable[SellCards]
      case ActionChoice.TakeCamels => Enumerable[TakeCamels.type]
    }

    if (enum.allValidMoves(state).isEmpty)
      Some(s"There are no possible moves for $choice.")
    else None
  }


  override def run(state: GameState): GameState = state

  override def nextEvent(state: GameState): Event = choice match {
    case ActionChoice.TakeCard => GetPlayerInput[TakeSingleCard]
    case ActionChoice.Exchange => GetPlayerInput[ExchangeCards]
    case ActionChoice.Sell => GetPlayerInput[SellCards]
    case ActionChoice.TakeCamels => TakeCamels
  }
}

object ChooseAction {
  implicit val enum = new Enumerable[ChooseAction] {
    override def allPossibleMoves: Seq[ChooseAction] =
      ActionChoice.values.map(ChooseAction.apply)
  }
}

