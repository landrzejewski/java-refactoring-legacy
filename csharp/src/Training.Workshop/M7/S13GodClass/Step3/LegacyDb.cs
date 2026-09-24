namespace Training.Workshop.M7.S13GodClass.Step3;

/// <summary>"Baza danych" starego systemu - globalne, mutowalne słowniki. Krok 3: rezerwacje jako typ Booking.</summary>
public static class LegacyDb
{
    public static readonly Dictionary<string, object?[]> Screenings = new();
    // internal: typ Booking jest wewnętrzny (C# nie pozwala wystawić go publicznym polem).
    internal static readonly Dictionary<string, Booking> Bookings = new();
    public static readonly Dictionary<string, int> Loyalty = new();
    public static int Sequence = 1;

    public static void Clear()
    {
        Screenings.Clear();
        Bookings.Clear();
        Loyalty.Clear();
        Sequence = 1;
        LegacyMailer.Sent.Clear();
        LegacyPaymentGateway.Charges.Clear();
    }
}
