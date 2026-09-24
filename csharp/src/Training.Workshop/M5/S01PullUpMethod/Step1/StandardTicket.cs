using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step1;

/// <summary>Krok 1: bez zmian - ta wersja Label() jest wzorcem dla pozostałych.</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public Money Price()
    {
        return BasePrice;
    }

    public string Label()
    {
        return Title + ": " + Price();
    }
}
