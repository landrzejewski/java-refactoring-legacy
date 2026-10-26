package pl.training.examples.structural.bridge;

public class EmailSender implements MessageSender {

    @Override
    public void send(String recipient, String content) {
        System.out.println("Email to " + recipient + ": " + content);
    }

}
