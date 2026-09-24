namespace Training.Workshop.M8.S08CompilerGate.Step1;

/// <summary>
/// Krok 1 (bez zmian): raport obłożenia. Nadal woła przestarzałe PriceTable.BasePrice(int) (CS0618)
/// i celowo "przelatuje" z IMAX do 3D (goto case, martwy break - CS0162) - IMAX ma też dźwięk Dolby.
/// </summary>
public sealed class OccupancyReport
{
    public string Describe(SeatMap map, int format)
    {
        string features = "";
        switch (format)
        {
            case 3:
                features = features + "duzy ekran, ";
                goto case 2;
                break;
            case 2:
                features = features + "dzwiek Dolby";
                break;
            default:
                features = "standard";
                break;
        }
        string name = format == 3 ? "IMAX" : format == 2 ? "3D" : "2D";
        return name + " [" + features + "], cena " + PriceTable.BasePrice(format)
            + " zl, zajete: " + Show(map.TakenPerRow());
    }

    private static string Show(IReadOnlyDictionary<int, int> takenPerRow)
    {
        return "{" + string.Join(", ", takenPerRow.Select(entry => entry.Key + "=" + entry.Value)) + "}";
    }
}
