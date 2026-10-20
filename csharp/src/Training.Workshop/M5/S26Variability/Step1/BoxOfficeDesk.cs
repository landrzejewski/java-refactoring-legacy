using System.Text.RegularExpressions;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step1;

/// <summary>
/// Krok 1: oś "cena wg typu biletu" wydzielona jako Strategy - TicketPricings wybiera strategię
/// po kodzie typu, a pulpit tylko ją woła. Operacje i cykl życia bez zmian (jedna oś naraz).
/// </summary>
public sealed class BoxOfficeDesk
{
    private static readonly IReadOnlyDictionary<string, Money> Formats = new Dictionary<string, Money>
    {
        ["2D"] = Money.Of("25.00"),
        ["3D"] = Money.Of("32.00"),
        ["IMAX"] = Money.Of("40.00"),
    };

    private readonly OrderedDictionary<string, Status> _statuses = [];
    private readonly OrderedDictionary<string, Money> _amounts = [];
    private readonly List<Entry> _journal = [];
    private Money _till = Money.Zero;

    public string Handle(string line)
    {
        var args = Regex.Split(line.Trim(), " +");
        var operation = args[0].ToUpperInvariant();
        try
        {
            return operation switch
            {
                "BOOK" => Book(args),
                "PAY" or "USE" or "CANCEL" => Change(operation, args),
                "UNDO" => Undo(),
                "REPORT" => Report(),
                _ => "Nieznana operacja: " + args[0],
            };
        }
        catch (Exception exception) when (exception is ArgumentException or InvalidOperationException)
        {
            return "Blad: " + exception.Message;
        }
    }

    private string Book(string[] args)
    {
        if (args.Length < 4)
        {
            throw new ArgumentException("BOOK <id> <format> <typ>...");
        }
        if (!Formats.TryGetValue(args[2], out var basePrice))
        {
            throw new ArgumentException("unknown format: " + args[2]);
        }
        var total = Money.Zero;
        for (var i = 3; i < args.Length; i++)
        {
            total = total.Plus(TicketPricings.ForType(args[i]).Invoke(basePrice));
        }
        if (_statuses.ContainsKey(args[1]))
        {
            throw new ArgumentException("booking exists: " + args[1]);
        }
        _statuses[args[1]] = Status.New;
        _amounts[args[1]] = total;
        _journal.Add(new Entry("BOOK", args[1], null, Money.Zero));
        return args[1] + " NEW " + total;
    }

    private string Change(string operation, string[] args)
    {
        if (args.Length < 2)
        {
            throw new ArgumentException(operation + " <id>");
        }
        var id = args[1];
        if (!_statuses.TryGetValue(id, out var status))
        {
            throw new ArgumentException("no booking " + id);
        }
        var amount = _amounts[id];
        var moved = Money.Zero;
        Status? next = null;
        switch (status)
        {
            case Status.New:
                switch (operation)
                {
                    case "PAY":
                        _till = _till.Plus(amount);
                        moved = amount;
                        next = Status.Paid;
                        break;
                    case "CANCEL":
                        next = Status.Cancelled;
                        break;
                }
                break;
            case Status.Paid:
                switch (operation)
                {
                    case "USE":
                        next = Status.Used;
                        break;
                    case "CANCEL":
                        _till = _till.Minus(amount);
                        moved = amount;
                        next = Status.Cancelled;
                        break;
                }
                break;
            case Status.Used:
            case Status.Cancelled:
                break;
        }
        if (next == null)
        {
            throw new InvalidOperationException("cannot " + operation.ToLowerInvariant() + " in " + status.ToString().ToUpperInvariant());
        }
        _statuses[id] = next.Value;
        _journal.Add(new Entry(operation, id, status, moved));
        if (operation == "PAY")
        {
            return id + " " + next.Value.ToString().ToUpperInvariant() + " " + moved;
        }
        return id + " " + next.Value.ToString().ToUpperInvariant() + (moved.IsGreaterThan(Money.Zero) ? " zwrot " + moved : "");
    }

    private string Undo()
    {
        if (_journal.Count == 0)
        {
            throw new InvalidOperationException("nothing to undo");
        }
        var last = _journal[^1];
        _journal.RemoveAt(_journal.Count - 1);
        switch (last.Operation)
        {
            case "BOOK":
                _statuses.Remove(last.Id);
                _amounts.Remove(last.Id);
                break;
            case "PAY":
                _till = _till.Minus(last.Moved);
                _statuses[last.Id] = last.Previous!.Value;
                break;
            case "CANCEL":
                _till = _till.Plus(last.Moved);
                _statuses[last.Id] = last.Previous!.Value;
                break;
            default:
                _statuses[last.Id] = last.Previous!.Value;
                break;
        }
        return "Cofnieto: " + last.Operation + " " + last.Id;
    }

    private string Report()
    {
        var bookings = _statuses.Count == 0 ? "-" : string.Join(", ",
            _statuses.Select(entry => entry.Key + "=" + entry.Value.ToString().ToUpperInvariant()));
        var log = _journal.Count == 0 ? "-" : string.Join(", ",
            _journal.Select(entry => entry.Operation + " " + entry.Id));
        return "Kasa: " + _till + "; " + bookings + "; dziennik: " + log;
    }

    /// <summary>Wpis dziennika: z niego liczone jest cofanie - osobny switch po nazwie operacji.</summary>
    private sealed record Entry(string Operation, string Id, Status? Previous, Money Moved);
}
