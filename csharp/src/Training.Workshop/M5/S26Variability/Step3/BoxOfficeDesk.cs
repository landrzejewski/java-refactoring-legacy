using System.Text.RegularExpressions;

namespace Training.Workshop.M5.S26Variability.Step3;

/// <summary>
/// Krok 3: trzy osie, trzy wzorce, zero mieszania. Pulpit zna tylko polecenia (Command)
/// i ich historię; cena biletu to Strategy wybierana po typie; cykl życia to State w Booking.
/// Nowy typ biletu, nowa operacja i nowy status zmieniają po jednym, rozłącznym miejscu.
/// </summary>
public sealed class BoxOfficeDesk
{
    private static readonly IReadOnlyDictionary<string, Func<string[], IDeskCommand>> Commands =
        new Dictionary<string, Func<string[], IDeskCommand>>
        {
            ["BOOK"] = BookCommand.From,
            ["PAY"] = PayCommand.From,
            ["USE"] = UseCommand.From,
            ["CANCEL"] = CancelCommand.From,
        };

    private readonly BookingLedger _ledger = new();
    private readonly List<IDeskCommand> _history = [];

    public string Handle(string line)
    {
        var args = Regex.Split(line.Trim(), " +");
        var operation = args[0].ToUpperInvariant();
        try
        {
            return operation switch
            {
                "UNDO" => Undo(),
                "REPORT" => Report(),
                _ => Execute(operation, args),
            };
        }
        catch (Exception exception) when (exception is ArgumentException or InvalidOperationException)
        {
            return "Blad: " + exception.Message;
        }
    }

    private string Execute(string operation, string[] args)
    {
        if (!Commands.TryGetValue(operation, out var factory))
        {
            return "Nieznana operacja: " + args[0];
        }
        var command = factory(args);
        var result = command.Execute(_ledger);
        _history.Add(command);
        return result;
    }

    private string Undo()
    {
        if (_history.Count == 0)
        {
            throw new InvalidOperationException("nothing to undo");
        }
        var last = _history[^1];
        _history.RemoveAt(_history.Count - 1);
        last.Undo(_ledger);
        return "Cofnieto: " + last.JournalEntry();
    }

    private string Report()
    {
        var log = _history.Count == 0 ? "-" : string.Join(", ",
            _history.Select(command => command.JournalEntry()));
        return _ledger.Summary() + "; dziennik: " + log;
    }
}
