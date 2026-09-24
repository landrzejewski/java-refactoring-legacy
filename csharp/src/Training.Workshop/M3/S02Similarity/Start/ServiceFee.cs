namespace Training.Workshop.M3.S02Similarity.Start;

/// <summary>
/// Start: "zDRYowana" opłata. Ktoś zauważył, że opłata rezerwacyjna online (2.00 za bilet)
/// i potrącenie przy zwrocie (3.00) wyglądają tak samo: "stała kwota razy liczba sztuk".
/// Powstała wspólna metoda z przełącznikiem, czyli fałszywa zależność między regułami
/// dwóch różnych właścicieli: sprzedaży online (marketing) i regulaminu zwrotów (obsługa klienta).
/// </summary>
public static class ServiceFee
{
    public enum Kind { OnlineBooking, Refund }

    public static decimal Of(Kind kind, int units)
    {
        var perUnit = kind == Kind.OnlineBooking ? 2.00m : 3.00m;
        return Math.Round(perUnit * units, 2, MidpointRounding.AwayFromZero);
    }
}
