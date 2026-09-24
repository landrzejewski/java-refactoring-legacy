namespace Training.Workshop.M4.S10EncapsulateField.Start;

/// <summary>
/// Start: status rezerwacji to publiczne pole. Reguły przejść (NEW -&gt; PAID -&gt; USED, ...)
/// pilnuje - czasem - kod klientów. Każdy może też po prostu przypisać dowolny tekst.
/// </summary>
public sealed class Reservation
{
    public string Status = "NEW";
}
