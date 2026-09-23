package pl.training.workshop.m6.s09_observer;

/** Port poczty - istniejąca integracja, której nie zmieniamy. */
public interface Mailer {
    void send(String to, String text);
}
