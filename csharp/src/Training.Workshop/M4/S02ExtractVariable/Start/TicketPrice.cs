namespace Training.Workshop.M4.S02ExtractVariable.Start;

/// <summary>
/// Start: cała cena biletu w jednym wyrażeniu. Działa, ale żeby odpowiedzieć na pytanie
/// "skąd 32.00?", trzeba w głowie policzyć pięć zagnieżdżonych ternary.
/// </summary>
public sealed class TicketPrice
{
    public decimal Price(TicketRequest r)
    {
        return Math.Round((r.Format == 3 ? 40.00m
                    : r.Format == 2 ? 32.00m : 25.00m)
                * (100 - (r.Type == "S" ? 25
                    : r.Type == "E" ? 30 : r.Type == "C" ? 40 : 0))
                / 100, 2, MidpointRounding.AwayFromZero)
            - (r.Start < new TimeOnly(12, 0)
                ? 5.00m : 0m)
            + (r.Row != null && r.Row.Value >= 10 ? 10.00m : 0m)
            + (r.Format == 2 && !r.OwnGlasses ? 3.00m : 0m);
    }
}
