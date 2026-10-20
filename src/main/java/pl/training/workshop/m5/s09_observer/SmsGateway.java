package pl.training.workshop.m5.s09_observer;

/** Port bramki SMS - istniejąca integracja, której nie zmieniamy. */
public interface SmsGateway {
    void send(String phone, String text);
}
