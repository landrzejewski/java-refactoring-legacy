package pl.training.workshop.m5.s05_extractsubclass.step4;

import pl.training.workshop.shared.Money;

/** Krok 4: premiera = seans + gość + dopłata 15.00. Żadnego "if (premiere)". */
public final class PremiereScreening extends Screening {
    private static final Money PREMIERE_SURCHARGE = Money.of("15.00");

    private final String guest;

    PremiereScreening(String title, String format, String guest) {
        super(title, format);
        this.guest = guest;
    }

    @Override
    public Money price() {
        return super.price().plus(PREMIERE_SURCHARGE);
    }

    @Override
    public String describe() {
        return super.describe() + " - premiera, gość: " + guest;
    }
}
