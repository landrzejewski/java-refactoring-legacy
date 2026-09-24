namespace Training.Workshop.M8.S11StagedRollout.Start;

/// <summary>
/// Start: wdrożenie nowego procesu płatności "na flagę". Stała w kodzie (zmiana = nowe wydanie),
/// lista testerów wpisana w if, brak podziału na etapy i brak wyłącznika awaryjnego.
/// </summary>
public sealed class CheckoutRouter
{
    internal static readonly bool NewCheckout = false;

    public bool UseNewCheckout(string email)
    {
        if (NewCheckout)
        {
            return true;
        }
        return email.Equals("anna@kino.pl") || email.Equals("jan@kino.pl");
    }
}
