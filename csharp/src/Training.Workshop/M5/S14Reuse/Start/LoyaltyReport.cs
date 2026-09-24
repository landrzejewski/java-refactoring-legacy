namespace Training.Workshop.M5.S14Reuse.Start;

/// <summary>Start: raport potrzebuje tylko właściciela i punktów - a przyjmuje całe LoyaltyAccount.</summary>
public sealed class LoyaltyReport
{
    public string Line(LoyaltyAccount account)
    {
        return account.Owner + ": " + account.Points + " pkt";
    }
}
