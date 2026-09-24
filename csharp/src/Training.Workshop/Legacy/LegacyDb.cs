namespace Training.Workshop.Legacy;

/// <summary>"Baza danych" starego systemu - globalne, mutowalne słowniki.</summary>
public static class LegacyDb
{
    public static readonly Dictionary<string, object?[]> Screenings = new();
    public static readonly Dictionary<string, object?[]> Bookings = new();
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
