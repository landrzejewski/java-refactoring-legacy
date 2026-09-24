namespace Training.Workshop.M5.S14Reuse.Step1;

/// <summary>Krok 1: bez zmian - nadal dziedziczy (i nadal łamie zastępowalność).</summary>
public class CorporateAccount : LoyaltyAccount
{
    public CorporateAccount(string company) : base(company)
    {
    }

    public override bool RedeemFreeTicket()
    {
        throw new NotSupportedException("konto firmowe nie wymienia punktów na bilety");
    }
}
