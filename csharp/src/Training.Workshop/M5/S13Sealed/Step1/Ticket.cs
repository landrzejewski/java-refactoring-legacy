using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step1;

/// <summary>
/// Krok 1: zamknięta hierarchia - C# nie ma <c>sealed ... permits</c>, więc zamykamy ją konstruktorem
/// <c>private protected</c>: warianty można dopisać tylko w tym assembly, a każdy wariant jest <c>sealed record</c>.
/// Kod spoza assembly nie może już dziedziczyć po Ticket.
/// </summary>
public abstract record Ticket
{
    private protected Ticket(Money basePrice)
    {
        BasePrice = basePrice;
    }

    public Money BasePrice { get; }
}
