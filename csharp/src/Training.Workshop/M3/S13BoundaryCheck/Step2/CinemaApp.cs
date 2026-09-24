using System.Globalization;
using Training.Workshop.M3.S13BoundaryCheck.Step2.Adapter;
using Training.Workshop.M3.S13BoundaryCheck.Step2.Domain;

namespace Training.Workshop.M3.S13BoundaryCheck.Step2;

/// <summary>
/// Punkt startowy wariantu (composition root): składa obiekty i opisuje seans -
/// cena, wiersz bazy, zgodność odczytu. Test woła tylko tę metodę.
/// </summary>
public static class CinemaApp
{
    public static string Describe(string title, DateTime start)
    {
        var service = new ScreeningService(25.00m, 5.00m);
        var mapper = new ScreeningRowMapper("screenings");
        var screening = new Screening(title, start);
        var row = mapper.ToRow(screening);
        return string.Create(CultureInfo.InvariantCulture,
            $"{service.Price(screening)} | {row} | {mapper.FromRow(row).Equals(screening)}");
    }
}
