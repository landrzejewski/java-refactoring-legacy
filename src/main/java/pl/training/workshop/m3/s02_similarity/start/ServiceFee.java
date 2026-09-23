package pl.training.workshop.m3.s02_similarity.start;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Start: "zDRYowana" opłata. Ktoś zauważył, że opłata rezerwacyjna online (2.00 za bilet)
 * i potrącenie przy zwrocie (3.00) wyglądają tak samo: "stała kwota razy liczba sztuk".
 * Powstała wspólna metoda z przełącznikiem, czyli fałszywa zależność między regułami
 * dwóch różnych właścicieli: sprzedaży online (marketing) i regulaminu zwrotów (obsługa klienta).
 */
public final class ServiceFee {
    public enum Kind { ONLINE_BOOKING, REFUND }

    private ServiceFee() {
    }

    public static BigDecimal of(Kind kind, int units) {
        BigDecimal perUnit = kind == Kind.ONLINE_BOOKING ? new BigDecimal("2.00") : new BigDecimal("3.00");
        return perUnit.multiply(BigDecimal.valueOf(units)).setScale(2, RoundingMode.HALF_UP);
    }
}
