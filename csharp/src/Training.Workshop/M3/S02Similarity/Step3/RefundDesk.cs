namespace Training.Workshop.M3.S02Similarity.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): potrącenie przy zwrocie to reguła regulaminu zwrotów
/// (właściciel: obsługa klienta, prawnik). Własna stała, własny powód zmiany.
/// </summary>
public sealed class RefundDesk
{
    private const decimal RefundDeduction = 3.00m;

    public decimal Refund(decimal paidForTickets, int percent)
    {
        var share = Math.Round(paidForTickets * percent / 100, 2, MidpointRounding.AwayFromZero);
        return Math.Max(share - RefundDeduction, 0.00m);
    }
}
