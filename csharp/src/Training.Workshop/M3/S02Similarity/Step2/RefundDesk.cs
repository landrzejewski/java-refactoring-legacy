namespace Training.Workshop.M3.S02Similarity.Step2;

/// <summary>Krok 2: Simplify - zostało to, co naprawdę mówi regulamin zwrotów: jedno potrącenie.</summary>
public sealed class RefundDesk
{
    public decimal Refund(decimal paidForTickets, int percent)
    {
        var share = Math.Round(paidForTickets * percent / 100, 2, MidpointRounding.AwayFromZero);
        return Math.Max(share - 3.00m, 0.00m);
    }
}
