namespace Training.Workshop.M8.S08CompilerGate.Start;

/// <summary>
/// Start: raport obłożenia sali. Woła przestarzałe PriceTable.BasePrice(int) (CS0618) i celowo
/// "przelatuje" z IMAX do 3D (goto case) - IMAX ma też dźwięk Dolby. Martwy break po goto,
/// przeniesiony z wersji z przelotem, to kod nieosiągalny (CS0162).
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
