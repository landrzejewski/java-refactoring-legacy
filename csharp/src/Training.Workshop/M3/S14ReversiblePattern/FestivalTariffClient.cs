namespace Training.Workshop.M3.S14ReversiblePattern;

/// <summary>
/// Obcy interfejs (stabilny, nie nasz): klient systemu festiwalu filmowego.
/// Zwraca tygodniową opłatę w groszach jako long - inny model niż nasz decimal.
/// </summary>
public sealed class FestivalTariffClient
{
    public long WeeklyFeeInCents(string title, int week)
    {
        return week <= 2 ? 30_000 : 15_000;
    }
}
