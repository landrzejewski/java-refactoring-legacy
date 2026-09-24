namespace Training.Workshop.M8.S08CompilerGate.Start;

/// <summary>Start: stare API z kodami int oznaczone jako przestarzałe, obok nowe API z nazwą formatu.</summary>
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
