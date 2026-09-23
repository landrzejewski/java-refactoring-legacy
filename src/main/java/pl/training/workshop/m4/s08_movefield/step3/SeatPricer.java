package pl.training.workshop.m4.s08_movefield.step3;

import java.math.BigDecimal;

/** Krok 3: wycena pyta seans "czy VIP" jeden raz, zamiast porównywać surowy próg. */
public final class SeatPricer {
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");

    public String quote(Screening screening, int row) {
        BigDecimal base = switch (screening.format()) {
            case 3 -> new BigDecimal("40.00");
            case 2 -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        boolean vip = screening.isVip(row);
        BigDecimal price = vip ? base.add(VIP_SURCHARGE) : base;
        return screening.hall().name() + ", rzad " + row + (vip ? " (VIP)" : "") + ": " + price;
    }
}
