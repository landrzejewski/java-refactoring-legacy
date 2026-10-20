using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step3;

/// <summary>
/// Krok 3 (bez zmian od kroku 2): CANCEL jako polecenie. Ile oddać, decyduje odbiorca (to zależy od statusu);
/// polecenie tylko zapamiętuje wynik, żeby cofnięcie wpłaciło go z powrotem.
/// </summary>
public sealed class CancelCommand : IDeskCommand
{
    private readonly string _id;
    private Status _previous;
    private Money _refunded = Money.Zero;

    private CancelCommand(string id)
    {
        _id = id;
    }

    internal static CancelCommand From(string[] args)
    {
        return new CancelCommand(IDeskCommand.IdFrom(args, "CANCEL"));
    }

    public string Execute(BookingLedger ledger)
    {
        _previous = ledger.StatusOf(_id);
        _refunded = ledger.Cancel(_id);
        return _id + " CANCELLED" + (_refunded.IsGreaterThan(Money.Zero) ? " zwrot " + _refunded : "");
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Deposit(_refunded);
        ledger.Restore(_id, _previous);
    }

    public string JournalEntry()
    {
        return "CANCEL " + _id;
    }
}
