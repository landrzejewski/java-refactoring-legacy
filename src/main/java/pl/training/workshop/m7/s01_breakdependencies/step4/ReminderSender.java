package pl.training.workshop.m7.s01_breakdependencies.step4;

/** Najwęższy seam dla powiadomień: interfejs funkcyjny, w teście wystarczy lambda. */
@FunctionalInterface
public interface ReminderSender {
    void send(String to, String subject, String body);
}
