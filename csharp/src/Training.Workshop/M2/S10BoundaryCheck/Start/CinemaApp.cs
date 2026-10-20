using System.Globalization;
using Training.Workshop.M2.S10BoundaryCheck.Start.Domain;

namespace Training.Workshop.M2.S10BoundaryCheck.Start;

/// <summary>
/// Punkt startowy wariantu (composition root): składa obiekty i opisuje seans -
/// cena, wiersz bazy, zgodność odczytu. Test woła tylko tę metodę.
/// </summary>
public static class CinemaApp
{
    public static string Describe(string title, DateTime start)
    {
        var service = new ScreeningService(25.00m, 5.00m, "screenings");
        var screening = new Screening(title, start);
        var row = service.ToRow(screening);
        return string.Create(CultureInfo.InvariantCulture,
            $"{service.Price(screening)} | {row} | {service.FromRow(row).Equals(screening)}");
    }
}
