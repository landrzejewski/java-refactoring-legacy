namespace Training.Workshop.M6.S03TypeCode.Step1;

/// <summary>
/// Krok 1: nowy typ dla kodu. Enum wystarcza - zestaw formatów jest mały i zamknięty.
/// Trwały kod int jest jawnym przypisaniem (<see cref="FormatExtensions"/>), nie <c>(int)format</c>.
/// </summary>
public enum Format
{
    TwoD,
    ThreeD,
    Imax,
}

/// <summary>Krok 1: operacje typu Format (C# 14: członkowie rozszerzeń - enum nie ma własnych metod).</summary>
public static class FormatExtensions
{
    extension(Format format)
    {
        public int Code => format switch
        {
            Format.TwoD => 1,
            Format.ThreeD => 2,
            Format.Imax => 3,
            _ => throw new ArgumentOutOfRangeException(nameof(format)),
        };

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
