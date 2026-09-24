namespace Training.Workshop.M7.S13GodClass.Step2;

/// <summary>
/// Krok 1: cennik wydzielony z CinemaManager.Book(). Kod przeniesiony dosłownie
/// (łącznie z double i kolejnością operacji) - poprawianie typu pieniędzy to osobna decyzja (s14).
/// </summary>
internal sealed class PricingService
{
    // format: 1 = 2D, 2 = 3D, 3 = IMAX; typ biletu: N, S, E, C

    internal double TicketsSum(int format, DateTime start, int vipFromRow,
        string[] seats, string[] types, bool ownGlasses)
    {
        double sum = 0;
        for (int i = 0; i < seats.Length; i++)
        {
            double p = 0;
            if (format == 1)
            {
                p = 25.00;
            }
            else if (format == 2)
            {
                p = 32.00;
            }
            else if (format == 3)
            {
                p = 40.00;
            }
            if (types[i] == "S")
            {
                p = p - p * 0.25;
            }
            else if (types[i] == "E")
            {
                p = p - p * 0.30;
            }
            else if (types[i] == "C")
            {
                p = p - p * 0.40;
            }
            if (start.Hour < 12)
            {
                p = p - 5;
            }
            if (int.Parse(seats[i].Substring(1)) >= vipFromRow)
            {
                p = p + 10;
            }
            if (format == 2 && !ownGlasses)
            {
                p = p + 3;
            }
            sum = sum + p;
        }
        if (seats.Length >= 10)
        {
            sum = sum - sum * 0.10;
        }
        return Math.Floor(sum * 100 + 0.5) / 100.0;
    }

    internal double BookingFee(bool web, int tickets)
    {
        return web ? 2.00 * tickets : 0;
    }
}
