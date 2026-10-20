namespace Training.Workshop.M5.S26Variability.Step3;

/// <summary>
/// Krok 3 (bez zmian od kroku 2): Command - żądanie kasjera jako obiekt. Wykonanie, cofnięcie i wpis do dziennika
/// są w jednym miejscu; polecenie pamięta tylko to, czego potrzebuje do cofnięcia.
/// </summary>
public interface IDeskCommand
{
    string Execute(BookingLedger ledger);

    void Undo(BookingLedger ledger);

    string JournalEntry();

    /// <summary>Wspólna walidacja poleceń "OPERACJA &lt;id&gt;".</summary>
    static string IdFrom(string[] args, string operation)
    {
        if (args.Length < 2)
        {
            throw new ArgumentException(operation + " <id>");
        }
        return args[1];
    }
}
