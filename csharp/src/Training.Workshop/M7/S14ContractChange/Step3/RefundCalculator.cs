using System.Globalization;

namespace Training.Workshop.M7.S14ContractChange.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): świadoma decyzja zamiast "przy okazji".
/// - Format "0.00" to kontrakt (paragony, e-maile) - przywrócony: zaokrąglenie PO Max(), a w C#
///   dodatkowo jawny format "F2", bo Math.Round nie dopisuje zer do decimal (0m zostaje "0").
/// - Zaokrąglenie AwayFromZero (HALF_UP) na decimal to reguła domeny; różnica groszowa względem double
///   została uzgodniona z księgowością i zatwierdzona jako ZMIANA KONTRAKTU w osobnym commicie
///   (test S14ContractTest ma dla niej jawnie nowe oczekiwanie).
/// </summary>
public sealed class RefundCalculator
{
    private const decimal Fee = 3.00m;

    public string Refund(double ticketsPaid, long minutesBeforeStart)
    {
        var refund = Math.Round(
            Math.Max((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 0m),
            2, MidpointRounding.AwayFromZero);
        return refund.ToString("F2", CultureInfo.InvariantCulture);
    }

    private static decimal Share(long minutesBeforeStart)
    {
        if (minutesBeforeStart <= 0)
        {
            return 0m;
        }
        if (minutesBeforeStart >= 24 * 60)
        {
            return 1m;
        }
        return 0.5m;
    }
}
