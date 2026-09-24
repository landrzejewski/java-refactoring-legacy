namespace Training.Workshop.M5.S14Reuse.Start;

/// <summary>
/// Start: konto firmowe dziedziczy tylko po to, by nie pisać drugi raz naliczania punktów.
/// Firma zbiera punkty do rocznego rabatu i NIE wymienia ich na bilety - więc odziedziczoną operację
/// blokuje wyjątkiem. Każdy klient LoyaltyAccount może dostać ten obiekt i wybuchnąć.
/// </summary>
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
