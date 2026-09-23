package pl.training.workshop.m7.s15_behaviourvector.step1;

/** Seam dla efektu ubocznego "wysłany mail" - w teście lambda zapisująca do dziennika. */
@FunctionalInterface
public interface Mailer {
    void send(String to, String text);
}
