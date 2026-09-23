package pl.training.workshop.m5.s05_extractsubclass.step3;

/**
 * Krok 3: Push Down - najpierw zachowanie (override describe()), potem stan (pole guest).
 * Gość jest wymagany w każdej premierze - null nie ma już gdzie się schować.
 */
public final class PremiereScreening extends Screening {
    private final String guest;

    PremiereScreening(String title, String format, String guest) {
        super(title, format, true);
        this.guest = guest;
    }

    @Override
    public String describe() {
        return super.describe() + " - premiera, gość: " + guest;
    }
}
