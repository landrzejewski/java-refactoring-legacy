package pl.training.workshop.exercises.e03_notifications.step3;

import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 3: wiadomosc o przeniesieniu seansu - jedna tresc zamiast trzech kopii. */
public final class MovedNotice extends Notice {
    private final Screening before;
    private final Screening after;

    public MovedNotice(Channel channel, Screening before, Screening after) {
        super(channel);
        this.before = before;
        this.after = after;
    }

    @Override
    protected String subject() {
        return "Zmiana seansu: " + after.title();
    }

    @Override
    protected String body() {
        return "Seans " + after.title() + " z " + before.start() + " przeniesiony na " + after.start()
                + ", sala " + after.hall() + ". Bilety zachowuja waznosc.";
    }
}
