namespace Training.Workshop.M3.S02Similarity.Step1;

/// <summary>Krok 1: Inline Method - kopia wspólnej logiki, jeszcze z przełącznikiem.</summary>
public sealed class RefundDesk
{
    public decimal Refund(decimal paidForTickets, int percent)
    {
        var share = Math.Round(paidForTickets * percent / 100, 2, MidpointRounding.AwayFromZero);
        var perUnit = ServiceFee.Kind.Refund == ServiceFee.Kind.OnlineBooking
            ? 2.00m : 3.00m;
        var fee = Math.Round(perUnit * 1, 2, MidpointRounding.AwayFromZero);
        return Math.Max(share - fee, 0.00m);
    }
}
