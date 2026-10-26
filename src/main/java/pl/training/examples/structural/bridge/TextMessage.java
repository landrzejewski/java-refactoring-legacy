package pl.training.examples.structural.bridge;

public class TextMessage extends Message {

    public TextMessage(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String text) {
        sender.send(recipient, text);
    }

}
