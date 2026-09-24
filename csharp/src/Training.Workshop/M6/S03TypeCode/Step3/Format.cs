using Training.Workshop.Shared;

namespace Training.Workshop.M6.S03TypeCode.Step3;

/// <summary>
/// Krok 3: typ domenowy bez wiedzy o kodach trwałych - mapowanie int przeniesione
/// do mappera FormatCodes (granica trwałości).
/// </summary>
public enum Format
{
    TwoD,
    ThreeD,
    Imax,
}

/// <summary>Krok 3: operacje typu Format (C# 14: członkowie rozszerzeń - enum nie ma własnych metod).</summary>
public static class FormatExtensions
{
    private static readonly Money TwoDPrice = Money.Of("25.00");
    private static readonly Money ThreeDPrice = Money.Of("32.00");
    private static readonly Money ImaxPrice = Money.Of("40.00");

    extension(Format format)
    {
        public string Label => format switch
        {
            Format.TwoD => "2D",
            Format.ThreeD => "3D",
            Format.Imax => "IMAX",
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

        public Money BasePrice => format switch
        {
            Format.TwoD => TwoDPrice,
            Format.ThreeD => ThreeDPrice,
            Format.Imax => ImaxPrice,
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

        public bool RequiresGlasses => format == Format.ThreeD;
    }
}
