package pl.training.module4.structural.bridge;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        var email = new EmailSender();
        var sms = new SmsSender();
        List<Message> messages = List.of(
                new TextMessage(email),
                new TextMessage(sms),
                new UrgentMessage(email),
                new UrgentMessage(sms));
        //------------------------------------------------
        for (var message : messages) {
            message.send("anna", "Server restarted");
        }
    }

}
