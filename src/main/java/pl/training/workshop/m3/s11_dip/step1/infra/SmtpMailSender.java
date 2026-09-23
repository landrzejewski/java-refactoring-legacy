package pl.training.workshop.m3.s11_dip.step1.infra;

import java.util.ArrayList;
import java.util.List;

/**
 * Szczegół techniczny: klient SMTP (symulowany). API mówi językiem protokołu:
 * surowa wiadomość MIME na wejściu, kod odpowiedzi SMTP na wyjściu.
 */
public final class SmtpMailSender {
    private final String host;
    private final int port;
    private final List<String> transcript = new ArrayList<>();

    public SmtpMailSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /** Symulacja: adres bez '@' daje 550, poprawny 250. */
    public String send(String to, String mimeMessage) {
        if (!to.contains("@")) {
            return "550 mailbox unavailable";
        }
        transcript.add(host + ":" + port + " " + mimeMessage);
        return "250 OK";
    }

    public List<String> transcript() {
        return List.copyOf(transcript);
    }
}
