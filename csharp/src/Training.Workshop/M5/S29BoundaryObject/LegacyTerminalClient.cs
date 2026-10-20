namespace Training.Workshop.M5.S29BoundaryObject;

/// <summary>
/// "Biblioteka" starego terminala płatniczego (nie zmieniamy jej): kwota w groszach, odpowiedź
/// jako tekst <c>STATUS;KOD;KWOTA_W_GROSZACH</c>, np. <c>OK;AUTH-R1;4500</c>.
/// Deterministyczna symulacja: kwota &lt;= 0 daje <c>ERROR;AMOUNT;0</c>, powyżej 1000.00
/// <c>DECLINED;51;0</c>, powyżej 300.00 autoryzację częściową do 300.00.
/// </summary>
public class LegacyTerminalClient
{
    public virtual string Authorize(string bookingId, long grosze)
    {
        if (grosze <= 0)
        {
            return "ERROR;AMOUNT;0";
        }
        if (grosze > 100_000)
        {
            return "DECLINED;51;0";
        }
        return "OK;AUTH-" + bookingId + ";" + Math.Min(grosze, 30_000);
    }
}
