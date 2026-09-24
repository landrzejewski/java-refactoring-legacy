using Training.Workshop.Shared;

namespace Training.Workshop.M6.S03TypeCode.Step2;

/// <summary>Krok 2: Move Method - zachowanie zależne od formatu przeniesione do typu Format.</summary>
public enum Format
{
    TwoD,
    ThreeD,
    Imax,
}

/// <summary>Krok 2: operacje typu Format (C# 14: członkowie rozszerzeń - enum nie ma własnych metod).</summary>
public static class FormatExtensions
{
    private static readonly Money TwoDPrice = Money.Of("25.00");
    private static readonly Money ThreeDPrice = Money.Of("32.00");
    private static readonly Money ImaxPrice = Money.Of("40.00");

    extension(Format format)
    {
        public int Code => format switch
        {
            Format.TwoD => 1,
            Format.ThreeD => 2,
            Format.Imax => 3,
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

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

        public static Format FromCode(int code)
        {
            foreach (var candidate in Enum.GetValues<Format>())
            {
                if (candidate.Code == code)
                {
                    return candidate;
                }
            }
            throw new ArgumentException("unknown format code: " + code);
        }
    }
}
