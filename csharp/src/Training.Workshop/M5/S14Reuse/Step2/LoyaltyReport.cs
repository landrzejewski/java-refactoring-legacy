namespace Training.Workshop.M5.S14Reuse.Step2;

/// <summary>Krok 2: raport zależy od roli IPointsHolder - przyjmuje oba konta bez fałszywego podtypowania.</summary>
public sealed class LoyaltyReport
{
    public string Line(IPointsHolder account)
    {
        return account.Owner + ": " + account.Points + " pkt";
    }
}
