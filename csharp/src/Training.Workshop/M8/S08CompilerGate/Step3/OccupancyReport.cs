namespace Training.Workshop.M8.S08CompilerGate.Step3;

/// <summary>
/// Krok 3: switch expression zamiast przelotu między case - intencja "IMAX ma też Dolby"
/// zapisana wprost. Zero ostrzeżeń: bramka (ostrzeżenia jako błędy) przechodzi.
/// </summary>
public sealed class OccupancyReport
{
    public string Describe(SeatMap map, int format)
    {
        string features = format switch
        {
            3 => "duzy ekran, dzwiek Dolby",
            2 => "dzwiek Dolby",
            _ => "standard",
        };
        string name = format == 3 ? "IMAX" : format == 2 ? "3D" : "2D";
        return name + " [" + features + "], cena " + PriceTable.BasePrice(name)
            + " zl, zajete: " + Show(map.TakenPerRow());
    }

    private static string Show(IReadOnlyDictionary<int, int> takenPerRow)
    {
        return "{" + string.Join(", ", takenPerRow.Select(entry => entry.Key + "=" + entry.Value)) + "}";
    }
}
