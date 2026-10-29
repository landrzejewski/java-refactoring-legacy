package pl.training.workshop.exercises.e03_notifications.step3;

import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 3: wiadomosc o odwolaniu seansu. */
public final class CancelledNotice extends Notice {
    private final Screening screening;
    private final String reason;

    public CancelledNotice(Channel channel, Screening screening, String reason) {
        super(channel);
        this.screening = screening;
        this.reason = reason;
    }

    @Override
    protected String subject() {
        return "Odwolany seans: " + screening.title();
    }

    @Override
    protected String body() {
        return "Seans " + screening.title() + " z " + screening.start() + " odwolany (" + reason
                + "). Zwrot w ciagu 7 dni.";
    }
}
