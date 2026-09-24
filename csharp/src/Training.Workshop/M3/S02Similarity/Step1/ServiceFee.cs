namespace Training.Workshop.M3.S02Similarity.Step1;

/// <summary>
/// Krok 1: po Inline Method z ServiceFee zostało tylko wyliczenie rodzajów.
/// Usuniemy je w kroku 2, gdy wywołujący przestaną z niego korzystać.
/// </summary>
public static class ServiceFee
{
    public enum Kind { OnlineBooking, Refund }
}
