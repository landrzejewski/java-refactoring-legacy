package pl.training.workshop.m3.s08_ocp.step2;

import java.math.BigDecimal;

/**
 * Krok 2: ScreeningOffer jest zamknięta na oś "format seansu" - nie zmieni się,
 * gdy dojdzie format. Nie jest zamknięta na inne osie (np. nowa dopłata za fotel
 * premium) i nie musi: zamykamy tylko oś, która faktycznie się zmienia.
 */
public final class ScreeningOffer {
    private static final BigDecimal GLASSES = new BigDecimal("3.00");

    public BigDecimal price(String code, boolean ownGlasses) {
        Format format = Format.parse(code);
        BigDecimal glasses = format.needsGlasses() && !ownGlasses ? GLASSES : BigDecimal.ZERO;
        return format.basePrice().add(glasses);
    }

    public String label(String code) {
        return Format.parse(code).label();
    }
}
