package pl.training.module4.structural.bridge;

public class SmsSender implements MessageSender {

    @Override
    public void send(String recipient, String content) {
        System.out.println("SMS to " + recipient + ": " + content);
    }

}
