package pl.training.module4.structural.bridge;

/**
 * Abstrakcja w moście: rodzaj wiadomości. Trzyma referencję do implementacji
 * ({@link MessageSender}) zamiast po niej dziedziczyć, dzięki czemu
 * 2 rodzaje wiadomości i 2 kanały to 4 klasy, a nie podklasa dla każdej kombinacji.
 */
public abstract class Message {

    protected final MessageSender sender;

    protected Message(MessageSender sender) {
        this.sender = sender;
    }

    public abstract void send(String recipient, String text);

}
