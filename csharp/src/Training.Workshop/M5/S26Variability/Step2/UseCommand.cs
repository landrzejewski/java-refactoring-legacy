namespace Training.Workshop.M5.S26Variability.Step2;

/// <summary>Krok 2: USE jako polecenie - cofnięcie przywraca tylko poprzedni status.</summary>
public sealed class UseCommand : IDeskCommand
{
    private readonly string _id;
    private Status _previous;

    private UseCommand(string id)
    {
        _id = id;
    }

    internal static UseCommand From(string[] args)
    {
        return new UseCommand(IDeskCommand.IdFrom(args, "USE"));
    }

    public string Execute(BookingLedger ledger)
    {
        _previous = ledger.StatusOf(_id);
        ledger.Use(_id);
        return _id + " USED";
    }

    public void Undo(BookingLedger ledger)
    {
        ledger.Restore(_id, _previous);
    }

    public string JournalEntry()
    {
        return "USE " + _id;
    }
}
