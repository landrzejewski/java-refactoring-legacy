using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step3;

/// <summary>Krok 3 (bez zmian od kroku 2): PAY jako polecenie - pamięta poprzedni status i pobraną kwotę, żeby je odwrócić.</summary>
public sealed class PayCommand : IDeskCommand
{
    private readonly string _id;
    private Status _previous;
    private Money _paid = Money.Zero;

    private PayCommand(string id)
    {
        _id = id;
    }

    internal static PayCommand From(string[] args)
    {
        return new PayCommand(IDeskCommand.IdFrom(args, "PAY"));
    }

    public string Execute(BookingLedger ledger)
    {
        _previous = ledger.StatusOf(_id);
        _paid = ledger.Pay(_id);
        return _id + " PAID " + _paid;
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Withdraw(_paid);
        ledger.Restore(_id, _previous);
    }

    public string JournalEntry()
    {
        return "PAY " + _id;
    }
}
