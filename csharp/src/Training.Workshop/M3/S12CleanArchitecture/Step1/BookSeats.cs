using System.Globalization;

namespace Training.Workshop.M3.S12CleanArchitecture.Step1;

/// <summary>
/// Krok 1: Extract Class - przypadek użycia jako jawna orkiestracja jednego celu:
/// wyceń, zapisz, powiadom. Wejście i wyjście to rekordy. Wciąż zna jednak
/// wiersz object[] i temat komunikatu (szczegóły techniczne).
/// </summary>
public sealed class BookSeats
{
    private readonly RowStore _db;
    private readonly Outbox _outbox;

    public BookSeats(RowStore db, Outbox outbox)
    {
        _db = db;
        _outbox = outbox;
    }

    public Booking Execute(BookSeatsCommand command)
    {
        if (command.Rows.Count == 0)
        {
            throw new ArgumentException("brak miejsc");
        }
        var total = Price(command);
        var id = _db.Insert(
            [command.Email, command.Format, command.Rows.Count, total]);
        _outbox.Publish("reservation-created",
            id + ";" + command.Email + ";" + total.ToString(CultureInfo.InvariantCulture));
        return new Booking(id, total);
    }

    private static decimal Price(BookSeatsCommand command)
    {
        var @base = command.Format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        var total = 0.00m;
        foreach (var row in command.Rows)
        {
            total += @base;
            if (row >= 10)
            {
                total += 10.00m;
            }
        }
        return total;
    }
}
