package pl.training.examples.structural.bridge;

public class UrgentMessage extends Message {

    public UrgentMessage(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String text) {
        sender.send(recipient, "[URGENT] " + text.toUpperCase());
    }

}
