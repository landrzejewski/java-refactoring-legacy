namespace Training.Workshop.M8.S08CompilerGate.Step3;

/// <summary>Krok 3 (bez zmian): stare API (przestarzałe) i nowe API cennika.</summary>
public static class PriceTable
{
    /// <summary>Cena bazowa wg kodu z CinemaManager.</summary>
    [Obsolete("kody int (1 = 2D, 2 = 3D, 3 = IMAX) - użyj BasePrice(string)")]
    public static int BasePrice(int format)
    {
        return format == 3 ? 40 : format == 2 ? 32 : 25;
    }

    public static int BasePrice(string format)
    {
        return format switch
        {
            "IMAX" => 40,
            "3D" => 32,
            _ => 25,
        };
    }
}
