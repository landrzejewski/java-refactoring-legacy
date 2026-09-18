namespace Training.Module8.Incremental;

/// <summary>
/// Odpowiednik <c>BigDecimal.setScale(scale, RoundingMode.HALF_EVEN)</c>:
/// zaokrągla i zawsze ustawia dokładnie podaną skalę (liczbę miejsc po przecinku).
/// </summary>
internal static class Money
{
    public static decimal SetScale(decimal value, int scale)
    {
        decimal rounded = Math.Round(value, scale, MidpointRounding.ToEven);
        // Dodanie zera o skali 'scale' wymusza tę skalę, np. 30 -> 30.00.
        return rounded + new decimal(0, 0, 0, false, (byte)scale);
    }
}
