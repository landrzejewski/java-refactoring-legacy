using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step2;

/// <summary>Krok 2: BOOK jako polecenie. Cenę liczą strategie z kroku 1, cofnięcie usuwa rezerwację.</summary>
public sealed class BookCommand : IDeskCommand
{
    private static readonly IReadOnlyDictionary<string, Money> Formats = new Dictionary<string, Money>
    {
        ["2D"] = Money.Of("25.00"),
        ["3D"] = Money.Of("32.00"),
        ["IMAX"] = Money.Of("40.00"),
    };

    private readonly string _id;
    private readonly string _format;
    private readonly IReadOnlyList<string> _ticketTypes;

    private BookCommand(string id, string format, IEnumerable<string> ticketTypes)
    {
        _id = id;
        _format = format;
        _ticketTypes = ticketTypes.ToList();
    }

    internal static BookCommand From(string[] args)
    {
        if (args.Length < 4)
        {
            throw new ArgumentException("BOOK <id> <format> <typ>...");
        }
        return new BookCommand(args[1], args[2], args[3..]);
    }

    public string Execute(BookingLedger ledger)
    {
        if (!Formats.TryGetValue(_format, out var basePrice))
        {
            throw new ArgumentException("unknown format: " + _format);
        }
        var total = Money.Zero;
        foreach (var type in _ticketTypes)
        {
            total = total.Plus(TicketPricings.ForType(type).Invoke(basePrice));
        }
        ledger.Open(_id, total);
        return _id + " NEW " + total;
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Remove(_id);
    }

    public string JournalEntry()
    {
        return "BOOK " + _id;
    }
}
