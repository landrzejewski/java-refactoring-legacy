using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Step3;

/// <summary>Krok 3: bez zmian - implementacje nie musiały nic robić, by dostać VatAmount().</summary>
public sealed class Ticket : IPriceable
{
    private readonly string _title;
    private readonly string _seat;
    private readonly Money _price;

    public Ticket(string title, string seat, Money price)
    {
        _title = title;
        _seat = seat;
        _price = price;
    }

    public string Title => _title;

    public string Seat => _seat;

    public Money Price => _price;

    /// <summary>Bilety: VAT 8%.</summary>
    public int VatPercent => 8;
}
