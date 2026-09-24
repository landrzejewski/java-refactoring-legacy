using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Step1;

/// <summary>Krok 1: bez zmian.</summary>
public abstract class Ticket
{
    private readonly string _title;
    private readonly Money _basePrice;

    protected Ticket(string title, Money basePrice)
    {
        _title = title;
        _basePrice = basePrice;
    }

    public string Title => _title;

    public Money BasePrice => _basePrice;
}
