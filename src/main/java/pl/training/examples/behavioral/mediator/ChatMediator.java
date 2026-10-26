package pl.training.examples.behavioral.mediator;

/**
 * Mediator: jedyny obiekt, który zna wszystkich uczestników. Uczestnicy
 * komunikują się wyłącznie przez niego, więc nie mają referencji do siebie nawzajem.
 */
public interface ChatMediator {

    void join(Participant participant);

    void broadcast(Participant sender, String text);

}
