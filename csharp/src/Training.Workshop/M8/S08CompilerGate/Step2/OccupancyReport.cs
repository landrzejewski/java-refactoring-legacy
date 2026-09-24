namespace Training.Workshop.M8.S08CompilerGate.Step2;

/// <summary>
/// Krok 2: nowe API cennika - nazwa formatu liczona raz i przekazana do BasePrice(string).
/// Zostaje ostrzeżenie CS0162 (kod nieosiągalny przy przelocie).
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
        return name + " [" + features + "], cena " + PriceTable.BasePrice(name)
            + " zl, zajete: " + Show(map.TakenPerRow());
    }

    private static string Show(IReadOnlyDictionary<int, int> takenPerRow)
    {
        return "{" + string.Join(", ", takenPerRow.Select(entry => entry.Key + "=" + entry.Value)) + "}";
    }
}
