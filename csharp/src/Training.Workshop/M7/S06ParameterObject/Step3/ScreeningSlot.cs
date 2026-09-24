using System.Globalization;

namespace Training.Workshop.M7.S06ParameterObject.Step3;

/// <summary>
/// Krok 3: walidacja przeniesiona do konstruktora rekordu - niepoprawny termin w ogóle nie powstaje.
/// To ZMIANA KONTRAKTU: wyjątek pojawia się wcześniej (przy tworzeniu obiektu) i także
/// dla Describe(), które wcześniej niczego nie sprawdzało. Dlatego osobny krok i osobny commit.
/// Rekord ma jawny konstruktor i właściwości tylko do odczytu (bez init), więc wyrażenie
/// <c>with</c> nie ominie walidacji.
/// </summary>
public sealed record ScreeningSlot
{
    private static readonly HashSet<string> Formats = ["2D", "3D", "IMAX"];

    public ScreeningSlot(string screeningId, DateOnly date, int hall, string format)
    {
        if (hall < 1 || hall > 8)
        {
            throw new ArgumentException("nie ma sali " + hall + " (" + screeningId + ")");
        }
        if (!Formats.Contains(format))
        {
            throw new ArgumentException("nieznany format " + format + " (" + screeningId + ")");
        }
        ScreeningId = screeningId;
        Date = date;
        Hall = hall;
        Format = format;
    }

    public string ScreeningId { get; }

    public DateOnly Date { get; }

    public int Hall { get; }

    public string Format { get; }

    public string Label()
    {
        return ScreeningId + " " + Date.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + " sala " + Hall + " (" + Format + ")";
    }
}
