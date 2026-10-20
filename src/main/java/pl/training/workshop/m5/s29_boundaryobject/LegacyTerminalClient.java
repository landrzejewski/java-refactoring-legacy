package pl.training.workshop.m5.s29_boundaryobject;

/**
 * "Biblioteka" starego terminala płatniczego (nie zmieniamy jej): kwota w groszach, odpowiedź
 * jako tekst {@code STATUS;KOD;KWOTA_W_GROSZACH}, np. {@code OK;AUTH-R1;4500}.
 * Deterministyczna symulacja: kwota &lt;= 0 daje {@code ERROR;AMOUNT;0}, powyżej 1000.00
 * {@code DECLINED;51;0}, powyżej 300.00 autoryzację częściową do 300.00.
 */
public class LegacyTerminalClient {
    public String authorize(String bookingId, long grosze) {
        if (grosze <= 0) {
            return "ERROR;AMOUNT;0";
        }
        if (grosze > 100_000) {
            return "DECLINED;51;0";
        }
        return "OK;AUTH-" + bookingId + ";" + Math.min(grosze, 30_000);
    }
}
