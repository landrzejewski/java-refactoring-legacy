using System.Globalization;

namespace Training.Workshop.M7.S14ContractChange.Step1;

/// <summary>
/// Krok 1: czysta refaktoryzacja - Extract Method Share() dla progów czasowych.
/// Nadal double, więc arytmetyka i zaokrąglenie są bit w bit takie same (x * 1.0 == x).
/// </summary>
public sealed class RefundCalculator
{
    public string Refund(double ticketsPaid, long minutesBeforeStart)
    {
        double refund = ticketsPaid * Share(minutesBeforeStart) - 3.00;
        if (refund < 0)
        {
            refund = 0;
        }
        refund = Math.Floor(refund * 100 + 0.5) / 100.0;
        return refund.ToString("F2", CultureInfo.InvariantCulture);
    }

    private static double Share(long minutesBeforeStart)
    {
        if (minutesBeforeStart <= 0)
        {
            return 0;
        }
        if (minutesBeforeStart >= 24 * 60)
        {
            return 1.0;
        }
        return 0.5;
    }
}
