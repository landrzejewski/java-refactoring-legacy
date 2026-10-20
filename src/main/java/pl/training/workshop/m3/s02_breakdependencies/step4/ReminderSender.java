package pl.training.workshop.m3.s02_breakdependencies.step4;

/** Najwęższy seam dla powiadomień: interfejs funkcyjny, w teście wystarczy lambda. */
@FunctionalInterface
public interface ReminderSender {
    void send(String to, String subject, String body);
}
