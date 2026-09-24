using System.Globalization;

namespace Training.Workshop.M7.S06ParameterObject.Start;

/// <summary>
/// Start: data clump - screeningId, date, hall, format zawsze podróżują razem.
/// Łatwo zamienić kolejność argumentów, a walidacja siedzi tylko w TicketPrice().
/// </summary>
public sealed class ScreeningPlanner
{
    public string Describe(string screeningId, DateOnly date, int hall, string format)
    {
        return screeningId + " " + date.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + " sala " + hall + " (" + format + ")";
    }

    public decimal TicketPrice(string screeningId, DateOnly date, int hall, string format)
    {
        if (hall < 1 || hall > 8)
        {
            throw new ArgumentException("nie ma sali " + hall + " (" + screeningId + ")");
        }
        return format switch
        {
            "2D" => 25.00m,
            "3D" => 32.00m,
            "IMAX" => 40.00m,
            _ => throw new ArgumentException("nieznany format " + format + " (" + screeningId + ")"),
        };
    }
}
