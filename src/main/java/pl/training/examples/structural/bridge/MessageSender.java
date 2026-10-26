package pl.training.examples.structural.bridge;

/**
 * Implementacja w moście: kanał, którym fizycznie wysyłamy wiadomość.
 * Rozwija się niezależnie od hierarchii {@link Message}.
 */
public interface MessageSender {

    void send(String recipient, String content);

}
