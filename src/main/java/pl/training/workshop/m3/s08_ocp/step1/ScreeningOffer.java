package pl.training.workshop.m3.s08_ocp.step1;

import java.math.BigDecimal;

/**
 * Krok 1: switch na enumie bez default. To jeszcze nie OCP, ale już bezpieczniejszy
 * zamknięty zbiór: nowa stała enuma = błąd kompilacji w każdym switchu, który jej nie obsłuży.
 * Dla małego, stabilnego zbioru to bywa wystarczający model.
 */
public final class ScreeningOffer {
    public BigDecimal price(String code, boolean ownGlasses) {
        Format format = Format.parse(code);
        BigDecimal base = switch (format) {
            case TWO_D -> new BigDecimal("25.00");
            case THREE_D -> new BigDecimal("32.00");
            case IMAX -> new BigDecimal("40.00");
        };
        BigDecimal glasses = switch (format) {
            case THREE_D -> ownGlasses ? BigDecimal.ZERO : new BigDecimal("3.00");
            case TWO_D, IMAX -> BigDecimal.ZERO;
        };
        return base.add(glasses);
    }

    public String label(String code) {
        return switch (Format.parse(code)) {
            case TWO_D -> "2D";
            case THREE_D -> "3D - okulary";
            case IMAX -> "IMAX - ekran laserowy";
        };
    }
}
