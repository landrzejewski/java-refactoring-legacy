using System.Globalization;

namespace Training.Workshop.M7.S14ContractChange.Start;

/// <summary>
/// Start: zwrot za anulowaną rezerwację na double - dokładnie tak jak w starym CinemaManager.
/// &gt;= 24h przed seansem 100%, mniej 50%, po starcie 0; potrącenie 3.00, nie poniżej zera.
/// Kontrakt to także FORMAT wyniku: zawsze dwa miejsca po przecinku, np. "0.00".
/// </summary>
public sealed class RefundCalculator
{
    public string Refund(double ticketsPaid, long minutesBeforeStart)
    {
        double refund;
        if (minutesBeforeStart <= 0)
        {
            refund = 0;
        }
        else if (minutesBeforeStart >= 24 * 60)
        {
            refund = ticketsPaid;
        }
        else
        {
            refund = ticketsPaid * 0.5;
        }
        refund = refund - 3.00;
        if (refund < 0)
        {
            refund = 0;
        }
        refund = Math.Floor(refund * 100 + 0.5) / 100.0;
        return refund.ToString("F2", CultureInfo.InvariantCulture);
    }
}
