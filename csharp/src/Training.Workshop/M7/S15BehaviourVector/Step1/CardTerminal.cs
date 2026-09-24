using Training.Workshop.Shared;

namespace Training.Workshop.M7.S15BehaviourVector.Step1;

/// <summary>
/// Statyczny terminal płatniczy. Karty kończące się na 0000 są odrzucane.
/// Obciążeń nie da się podejrzeć z testu.
/// </summary>
public static class CardTerminal
{
    public static bool Charge(string card, Money amount)
    {
        return !card.EndsWith("0000", StringComparison.Ordinal);
    }
}
