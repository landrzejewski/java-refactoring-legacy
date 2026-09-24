namespace Training.Workshop.M3.S02Similarity.Start;

/// <summary>Zwroty: procent zapłaconej kwoty minus potrącenie, nie mniej niż zero.</summary>
public sealed class RefundDesk
{
    public decimal Refund(decimal paidForTickets, int percent)
    {
        var share = Math.Round(paidForTickets * percent / 100, 2, MidpointRounding.AwayFromZero);
        // "1", bo potrącenie jest za zwrot, a nie za bilet - parametr pasuje tylko drugiej regule
        return Math.Max(share - ServiceFee.Of(ServiceFee.Kind.Refund, 1), 0.00m);
    }
}
