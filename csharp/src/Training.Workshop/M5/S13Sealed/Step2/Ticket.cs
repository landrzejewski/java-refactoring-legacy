using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step2;

/// <summary>Krok 2: bez zmian - zamknięta hierarchia pozwala sprawdzić wyczerpanie switch (test Roslyn).</summary>
public abstract record Ticket
{
    private protected Ticket(Money basePrice)
    {
        BasePrice = basePrice;
    }

    public Money BasePrice { get; }
}
