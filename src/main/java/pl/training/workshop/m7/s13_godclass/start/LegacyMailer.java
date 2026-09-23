package pl.training.workshop.m7.s13_godclass.start;

import java.util.ArrayList;
import java.util.List;

/** "Serwer SMTP" starego systemu - statyczna skrzynka nadawcza. */
public final class LegacyMailer {
    public static final List<String> SENT = new ArrayList<>();

    private LegacyMailer() {
    }

    public static void send(String to, String subject, String body) {
        SENT.add("MAIL to=" + to + " subject=" + subject + " body=" + body);
    }

    public static void sms(String phone, String text) {
        SENT.add("SMS to=" + phone + " text=" + text);
    }
}
