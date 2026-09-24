using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Start;

/// <summary>Start: bilet - koszyk potrzebuje z niego tylko ceny i stawki VAT, reszta API go nie obchodzi.</summary>
public sealed class Ticket
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
