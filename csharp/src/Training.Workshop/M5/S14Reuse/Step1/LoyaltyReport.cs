namespace Training.Workshop.M5.S14Reuse.Step1;

/// <summary>Krok 1: bez zmian.</summary>
public sealed class LoyaltyReport
{
    public string Line(LoyaltyAccount account)
    {
        return account.Owner + ": " + account.Points + " pkt";
    }
}
